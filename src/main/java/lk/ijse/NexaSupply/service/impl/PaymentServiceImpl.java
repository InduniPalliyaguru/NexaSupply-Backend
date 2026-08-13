package lk.ijse.NexaSupply.service.impl;

import lk.ijse.NexaSupply.dto.CreditLedgerDTO;
import lk.ijse.NexaSupply.dto.PaymentDTO;
import lk.ijse.NexaSupply.dto.PaymentProcessDTO;
import lk.ijse.NexaSupply.entity.Order;
import lk.ijse.NexaSupply.entity.Payment;
import lk.ijse.NexaSupply.entity.User;
import lk.ijse.NexaSupply.enumeration.LedgerType;
import lk.ijse.NexaSupply.enumeration.PaymentStatus;
import lk.ijse.NexaSupply.exception.CustomException;
import lk.ijse.NexaSupply.repository.PaymentRepository;
import lk.ijse.NexaSupply.repository.UserRepository;
import lk.ijse.NexaSupply.service.CreditLedgerService;
import lk.ijse.NexaSupply.service.NotificationService;
import lk.ijse.NexaSupply.service.PaymentService;
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
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final CreditLedgerService creditLedgerService;
    private final NotificationService notificationService;

    @Override
    public void createPendingPaymentForOrder(Order order) {
        log.info("Execute createPendingPaymentForOrder method");

        Payment payment = new Payment();

        payment.setPaymentCode(generatePaymentCode());
        payment.setTotalAmount(order.getTotalPrice());
        payment.setPaidAmount(0.0);
        payment.setBalanceAmount(order.getTotalPrice());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setOrder(order);

        paymentRepository.save(payment);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PaymentDTO processPayment(PaymentProcessDTO dto) {
        log.info("Execute processPayment method");

        Optional<Payment> optionalPayment = paymentRepository.findByPaymentCode(dto.getPaymentCode());
        if (optionalPayment.isEmpty()) {
            throw new CustomException(404, "Payment not found with code: " + dto.getPaymentCode());
        }
        Payment payment = optionalPayment.get();

        double newPaidAmount = payment.getPaidAmount() + dto.getPayingAmount();
        double newBalanceAmount = payment.getBalanceAmount() - dto.getPayingAmount();

        if (newBalanceAmount < 0) {
            throw new CustomException(400, "Paying amount exceeds the remaining balance!");
        }

        payment.setPaidAmount(newPaidAmount);
        payment.setBalanceAmount(newBalanceAmount);
        payment.setPaymentDate(LocalDateTime.now());

        if (newBalanceAmount == 0) {
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
        } else {
            payment.setPaymentStatus(PaymentStatus.PARTIAL);
        }

        User customer = payment.getOrder().getCustomer();
        if (customer == null) {
            throw new CustomException(404, "Customer not found!");
        }
        double newCreditLimit = customer.getCreditLimit() + dto.getPayingAmount();
        customer.setCreditLimit(newCreditLimit);
        userRepository.save(customer);

        Payment savedPayment = paymentRepository.save(payment);

        CreditLedgerDTO ledgerDTO = new CreditLedgerDTO();
        ledgerDTO.setReferenceCode(payment.getPaymentCode());
        ledgerDTO.setAmount(dto.getPayingAmount());
        ledgerDTO.setBalanceAfter(newCreditLimit);
        ledgerDTO.setLedgerType(LedgerType.PAYMENT_RESTORATION);
        ledgerDTO.setDescription("Credit limit recovered via payment: " + payment.getPaymentCode());
        ledgerDTO.setUserCode(customer.getUserCode());

        creditLedgerService.recordLedger(ledgerDTO);

        notificationService.createNotification(
                customer,
                "Payment Received",
                "Payment of LKR " + dto.getPayingAmount() + " processed for order " + payment.getOrder().getOrderCode() + ". Remaining balance: LKR " + newBalanceAmount + "."
        );

        return mapToDTO(savedPayment);
    }

    @Override
    public PaymentDTO getPaymentByCode(String paymentCode) {
        log.info("Execute getPaymentByCode method");

        Optional<Payment> optionalPayment = paymentRepository.findByPaymentCode(paymentCode);
        if (optionalPayment.isEmpty()) {
            throw new CustomException(404, "Payment not found with code: " + paymentCode);
        }
        Payment payment = optionalPayment.get();

        return mapToDTO(payment);
    }

    @Override
    public List<PaymentDTO> getAllPayments() {
        log.info("Execute getAllPayments method");

        List<Payment> all = paymentRepository.findAll();
        List<PaymentDTO> payDtoList = new ArrayList<>();
        for (Payment payment : all) {
            PaymentDTO paymentDTO = mapToDTO(payment);
            payDtoList.add(paymentDTO);
        }
        return payDtoList;
    }

    @Override
    public PaymentDTO getPaymentByOrderCode(String orderCode) {
        log.info("Execute getPaymentByOrderCode method");

        Optional<Payment> optionalPayment = paymentRepository.findByOrder_OrderCode(orderCode);
        if (optionalPayment.isEmpty()) {
            throw new CustomException(404, "Payment not found with order code: " + orderCode);
        }
        Payment payment = optionalPayment.get();
        return mapToDTO(payment);
    }

    public String generatePaymentCode() {
        int year = Year.now().getValue();
        long count = paymentRepository.countAllPayments() + 1;
        return String.format("PAY-%d-%04d", year, count);
    }

    private PaymentDTO mapToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setPaymentCode(payment.getPaymentCode());
        dto.setTotalAmount(payment.getTotalAmount());
        dto.setPaidAmount(payment.getPaidAmount());
        dto.setBalanceAmount(payment.getBalanceAmount());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setPaymentDate(payment.getPaymentDate());

        if (payment.getOrder() != null) {
            dto.setOrderCode(payment.getOrder().getOrderCode());
            if (payment.getOrder().getCustomer() != null) {
                dto.setCustomerName(payment.getOrder().getCustomer().getFullName());
            }
        }
        return dto;
    }


}
