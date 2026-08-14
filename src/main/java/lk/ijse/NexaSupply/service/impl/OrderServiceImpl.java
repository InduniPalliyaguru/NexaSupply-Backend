package lk.ijse.NexaSupply.service.impl;

import lk.ijse.NexaSupply.dto.*;
import lk.ijse.NexaSupply.entity.*;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import lk.ijse.NexaSupply.enumeration.LedgerType;
import lk.ijse.NexaSupply.enumeration.OrderStatus;
import lk.ijse.NexaSupply.enumeration.Role;
import lk.ijse.NexaSupply.exception.CustomException;
import lk.ijse.NexaSupply.repository.OrderRepository;
import lk.ijse.NexaSupply.repository.ProductRepository;
import lk.ijse.NexaSupply.repository.UserRepository;
import lk.ijse.NexaSupply.service.*;
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
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final ReportService reportService;

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

        notificationService.createNotification(
                customer,
                "Order Placed Successfully",
                "Your order " + savedOrder.getOrderCode() + " for LKR " + savedOrder.getTotalPrice() + " has been placed successfully."
        );

        List<User> adminList = userRepository.findByRoleAndDataStatus(Role.ROLE_ADMIN, DataStatus.ACTIVE);
        for (User admin : adminList) {
            notificationService.createNotification(
                    admin,
                    "New Order Received!",
                    "New order " + savedOrder.getOrderCode() + " placed by " + customer.getFullName() + " for LKR " + savedOrder.getTotalPrice() + "."
            );
        }

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
            Payment pendingPay = paymentService.createPendingPaymentForOrder(order);
            System.out.println("Pay COde" + pendingPay.getPaymentCode());
            order.setPayment(pendingPay);
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
        System.out.println("Pay COde" + savedOrder.getPayment().getPaymentCode());

        if (status == OrderStatus.APPROVED) {
            sendInvoiceEmail(savedOrder);
        }

        if (savedOrder.getCustomer() != null) {
            notificationService.createNotification(
                    savedOrder.getCustomer(),
                    "Order Status Updated",
                    "Your order " + orderCode + " status has been updated to " + status + "."
            );
        }

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

    @Override
    public OrderInvoiceReportDTO getOrderInvoiceReportData(String orderCode) {

        Optional<Order> optionalOrder = orderRepository.findByOrderCode(orderCode);
        if (optionalOrder.isEmpty()) {
            throw new CustomException(404, "Order Not Found with Order Code: " + orderCode);
        }
        Order order = optionalOrder.get();
        User customer = order.getCustomer();

        List<OrderItemReportDTO> dto = new ArrayList<>();
        if (order.getOrderProductList() != null) {

            List<OrderProduct> orderProductList = order.getOrderProductList();
            for (OrderProduct item : orderProductList) {

                OrderItemReportDTO itemDTO = new OrderItemReportDTO();
                itemDTO.setProductName(item.getProduct().getName());
                itemDTO.setUnitPrice(item.getUnitPrice());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setSubTotal(item.getUnitPrice() * item.getQuantity());
                dto.add(itemDTO);
            }
        }

        OrderInvoiceReportDTO reportDTO = new OrderInvoiceReportDTO();
        reportDTO.setOrderCode(order.getOrderCode());
        reportDTO.setOrderDate(order.getOrderDate() != null ? order.getOrderDate().toString() : LocalDateTime.now().toString());
        reportDTO.setOrderStatus(order.getOrderStatus().name());
        reportDTO.setCustomerName(customer.getFullName());
        reportDTO.setShopName(customer.getShopName() != null ? customer.getShopName() : "");
        reportDTO.setCustomerEmail(customer.getEmail());
        reportDTO.setCustomerPhone(customer.getPhone() != null ? customer.getPhone() : "N/A");
        reportDTO.setPayCode(order.getPayment().getPaymentCode());
        reportDTO.setPaymentStatus(order.getPayment().getPaymentStatus().name());
        reportDTO.setGrandTotal(order.getTotalPrice());
        reportDTO.setPaidAmount(order.getPayment().getPaidAmount());
        reportDTO.setBalanceAmount(order.getPayment().getBalanceAmount());
        reportDTO.setItems(dto);

        return reportDTO;
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

    private void sendInvoiceEmail(Order order) {
        try {

            User customer = order.getCustomer();
            if (customer == null || customer.getEmail() == null) {
                throw new CustomException(404, "Customer or Email is null for Order Code " + order.getOrderCode());
            }

            OrderInvoiceReportDTO reportDTO = getOrderInvoiceReportData(order.getOrderCode());
            byte[] pdfBytes = reportService.generateOrderInvoicePdf(reportDTO);

            String emailSubject = "Order Approved & Invoice - " + order.getOrderCode();

            String emailBody = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e8d5ea; border-radius: 8px; padding: 25px; background-color: #ffffff;'>"
                    + "<h2 style='color: #3b0a45; text-align: center; margin-bottom: 20px;'>Order Approved! 🎉</h2>"
                    + "<p style='color: #333333; font-size: 15px;'>Dear <b>" + customer.getFullName() + "</b>,</p>"
                    + "<p style='color: #555555; line-height: 1.5;'>Great news! Your order has been reviewed and <b>APPROVED</b> by our distribution team.</p>"

                    + "<div style='background-color: #faf2fc; border-left: 4px solid #6f2c91; padding: 18px; margin: 20px 0; border-radius: 6px;'>"
                    + "<h4 style='margin: 0 0 10px 0; color: #4a005b; text-transform: uppercase; letter-spacing: 0.5px;'>Order Details</h4>"
                    + "<p style='margin: 6px 0; color: #333333;'><b>Order Code:</b> " + order.getOrderCode() + "</p>"
                    + "<p style='margin: 6px 0; color: #333333;'><b>Order Status:</b> <span style='color: #6f2c91; font-weight: bold;'>APPROVED</span></p>"
                    + "</div>"

                    + "<p style='color: #555555; line-height: 1.5;'>Please find your official order invoice attached to this email as a PDF document for your reference.</p>"
                    + "<br/>"
                    + "<p style='color: #555555; margin: 0;'>Thank you for doing business with <b style='color: #4a005b;'>NexaSupply</b>!</p>"
                    + "<br/>"
                    + "<p style='color: #333333; margin: 0;'>Best Regards,<br/><b style='color: #4a005b;'>NexaSupply Distribution Team</b></p>"
                    + "</div>";

            String attachmentFileName = "Invoice_" + order.getOrderCode() + ".pdf";

            emailService.sendOrderInvoiceEmail(
                    customer.getEmail(),
                    emailSubject,
                    emailBody,
                    pdfBytes,
                    attachmentFileName
            );
            log.info("Invoice email send successfully");

        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(500, "Failed to send email");

        }
    }

}
