package lk.ijse.NexaSupply.service.impl;

import lk.ijse.NexaSupply.dto.CreditLedgerDTO;
import lk.ijse.NexaSupply.dto.OrderProductDTO;
import lk.ijse.NexaSupply.dto.OrderRequestDTO;
import lk.ijse.NexaSupply.dto.OrderResponseDTO;
import lk.ijse.NexaSupply.entity.*;
import lk.ijse.NexaSupply.enumeration.LedgerType;
import lk.ijse.NexaSupply.enumeration.OrderStatus;
import lk.ijse.NexaSupply.exception.CustomException;
import lk.ijse.NexaSupply.repository.OrderRepository;
import lk.ijse.NexaSupply.repository.ProductRepository;
import lk.ijse.NexaSupply.repository.UserRepository;
import lk.ijse.NexaSupply.service.AuditLogService;
import lk.ijse.NexaSupply.service.CreditLedgerService;
import lk.ijse.NexaSupply.service.OrderService;
import lk.ijse.NexaSupply.service.PaymentService;
import lk.ijse.NexaSupply.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final PaymentService paymentService;
    private final CreditLedgerService creditLedgerService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public OrderResponseDTO placeOrder(OrderRequestDTO requestDTO) {
        log.info("Execute Place Order Method");

        String currentUserEmail = SecurityUtils.getCurrentUserEmail();
        Optional<User> optionalUser = userRepository.findActiveByEmail(currentUserEmail);

        if (optionalUser.isEmpty()) {
            throw new CustomException(404, "Customer Not Found");
        }
        User customer = optionalUser.get();

        double calculatedTotal = 0.0;
        List<OrderProduct> orderProductList = new ArrayList<>();

        Order order = new Order();
        order.setOrderCode(generateOrderCode());
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setCustomer(customer);

        List<OrderProductDTO> orderItems = requestDTO.getOrderItems();

        for (OrderProductDTO itemDTO : orderItems) {

            Optional<Product> optionalProduct = productRepository.findActiveByProductCode(itemDTO.getProductCode());
            if (optionalProduct.isEmpty()) {
                throw new CustomException(404, "Product Not Found");
            }
            Product product = optionalProduct.get();

            if (product.getQuantity() < itemDTO.getQuantity()) {
                throw new CustomException(400, "Insufficient stock for product: " + product.getName());
            }

            double itemSubTotal = product.getUnitPrice() * itemDTO.getQuantity();
            calculatedTotal += itemSubTotal;

            int newQty = product.getQuantity() - itemDTO.getQuantity();
            product.setQuantity(newQty);
            productRepository.save(product);

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            orderProduct.setQuantity(itemDTO.getQuantity());
            orderProduct.setUnitPrice(product.getUnitPrice());

            orderProductList.add(orderProduct);
        }

        if (customer.getCreditLimit() < calculatedTotal) {
            throw new CustomException(400, "Insufficient credit limit! Available credit limit: " + customer.getCreditLimit());
        }

        double newCreditBalance = customer.getCreditLimit() - calculatedTotal;
        customer.setCreditLimit(newCreditBalance);
        userRepository.save(customer);

        order.setTotalPrice(calculatedTotal);
        order.setOrderProductList(orderProductList);

        Order savedOrder = orderRepository.save(order);

        CreditLedgerDTO ledgerDTO = new CreditLedgerDTO();
        ledgerDTO.setReferenceCode(savedOrder.getOrderCode());
        ledgerDTO.setAmount(calculatedTotal);
        ledgerDTO.setBalanceAfter(newCreditBalance);
        ledgerDTO.setLedgerType(LedgerType.ORDER_DEDUCTION);
        ledgerDTO.setDescription("Credit deducted for placing order: " + savedOrder.getOrderCode());
        ledgerDTO.setUserCode(customer.getUserCode());

        creditLedgerService.recordLedger(ledgerDTO);

        auditLogService.logAction(currentUserEmail, "PLACED_ORDER | Code: " + savedOrder.getOrderCode() + " | Total: " + savedOrder.getTotalPrice());

        return mapToResponseDTO(savedOrder);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public OrderResponseDTO updateOrderStatus(String orderCode, OrderStatus status) {
        log.info("Execute Update Order Status Method");

        Optional<Order> optionalOrder = orderRepository.findByOrderCode(orderCode);
        if (optionalOrder.isEmpty()) {
            throw new CustomException(404, "Order Not Found with Order Code: " + orderCode);
        }
        Order order = optionalOrder.get();

        if (status == OrderStatus.APPROVED && order.getOrderStatus() == OrderStatus.PENDING) {
            paymentService.createPendingPaymentForOrder(order);
        }

        if (status == OrderStatus.CANCELLED && order.getOrderStatus() != OrderStatus.CANCELLED) {
            User customer = order.getCustomer();

            double restoreCredit = customer.getCreditLimit() + order.getTotalPrice();
            customer.setCreditLimit(restoreCredit);
            userRepository.save(customer);

            CreditLedgerDTO ledgerDTO = new CreditLedgerDTO();
            ledgerDTO.setReferenceCode(order.getOrderCode());
            ledgerDTO.setAmount(order.getTotalPrice());
            ledgerDTO.setBalanceAfter(restoreCredit);
            ledgerDTO.setLedgerType(LedgerType.ORDER_CANCEL_REFUND);
            ledgerDTO.setDescription("Credit restored for cancelled order: " + orderCode);
            ledgerDTO.setUserCode(customer.getUserCode());

            creditLedgerService.recordLedger(ledgerDTO);

            List<OrderProduct> orderProductList = order.getOrderProductList();

            for (OrderProduct item : orderProductList) {
                Product product = item.getProduct();
                product.setQuantity(product.getQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }

        order.setOrderStatus(status);
        Order savedOrder = orderRepository.save(order);

        auditLogService.logAction(SecurityUtils.getCurrentUserEmail(), "UPDATED_ORDER_STATUS | Code: " + orderCode + " | Status: " + status);

        return mapToResponseDTO(savedOrder);
    }

    @Override
    public OrderResponseDTO getOrderByCode(String orderCode) {
        log.info("Execute Get Order Status Method");

        Optional<Order> optionalOrder = orderRepository.findByOrderCode(orderCode);
        if (optionalOrder.isEmpty()) {
            throw new CustomException(404, "Order Not Found with Order Code: " + orderCode);
        }

        return mapToResponseDTO(optionalOrder.get());
    }

    @Override
    public List<OrderResponseDTO> getMyOrders() {
        log.info("Execute Get My Order Status Method");

        String currentUserEmail = SecurityUtils.getCurrentUserEmail();
        List<Order> orderList = orderRepository.findByCustomer_EmailOrderByOrderDateDesc(currentUserEmail);

        List<OrderResponseDTO> responseList = new ArrayList<>();
        for (Order order : orderList) {
            OrderResponseDTO orderResponseDTO = mapToResponseDTO(order);
            responseList.add(orderResponseDTO);
        }
        return responseList;
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        log.info("Execute Get All Order Status Method");

        List<Order> orderList = orderRepository.findAllOrdersSortedByDate();
        List<OrderResponseDTO> responseList = new ArrayList<>();

        for (Order order : orderList) {
            OrderResponseDTO orderResponseDTO = mapToResponseDTO(order);
            responseList.add(orderResponseDTO);
        }
        return responseList;
    }

    private String generateOrderCode() {
        int year = Year.now().getValue();
        long count = orderRepository.countAllOrders() + 1;
        return String.format("ORD-%d-%04d", year, count);
    }

    private OrderResponseDTO mapToResponseDTO(Order order) {
        List<OrderProductDTO> items = new ArrayList<>();

        List<OrderProduct> orderProductList = order.getOrderProductList();
        for (OrderProduct item : orderProductList) {
            OrderProductDTO itemDTO = new OrderProductDTO(
                    item.getProduct().getProductCode(),
                    item.getQuantity()
            );
            items.add(itemDTO);
        }

        return new OrderResponseDTO(
                order.getOrderId(),
                order.getOrderCode(),
                order.getOrderDate(),
                order.getTotalPrice(),
                order.getOrderStatus(),
                order.getCustomer().getEmail(),
                items
        );
    }

}
