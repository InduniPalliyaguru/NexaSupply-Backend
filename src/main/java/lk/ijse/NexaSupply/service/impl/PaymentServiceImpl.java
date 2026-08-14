package lk.ijse.NexaSupply.service.impl;

import lk.ijse.NexaSupply.dto.*;
import lk.ijse.NexaSupply.entity.Order;
import lk.ijse.NexaSupply.entity.OrderProduct;
import lk.ijse.NexaSupply.entity.Payment;
import lk.ijse.NexaSupply.entity.User;
import lk.ijse.NexaSupply.enumeration.LedgerType;
import lk.ijse.NexaSupply.enumeration.PaymentStatus;
import lk.ijse.NexaSupply.exception.CustomException;
import lk.ijse.NexaSupply.repository.PaymentRepository;
import lk.ijse.NexaSupply.repository.UserRepository;
import lk.ijse.NexaSupply.service.*;
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
    private final EmailService emailService;
    private final ReportService reportService;

    @Override
    public Payment createPendingPaymentForOrder(Order order) {
        log.info("Execute createPendingPaymentForOrder method");

        Payment payment = new Payment();

        payment.setPaymentCode(generatePaymentCode());
        payment.setTotalAmount(order.getTotalPrice());
        payment.setPaidAmount(0.0);
        payment.setBalanceAmount(order.getTotalPrice());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setOrder(order);

        return paymentRepository.save(payment);
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
            sendFinalPaymentReceiptEmail(payment);
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

    private void sendFinalPaymentReceiptEmail(Payment payment) {

        try {

            Order order = payment.getOrder();
            if (order == null || order.getCustomer() == null) {
                throw new CustomException(404, "Order or customer not found for payment code: " + payment.getPaymentCode());
            }
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

            byte[] pdfBytes = reportService.generateOrderInvoicePdf(reportDTO);

            String subject = "Payment Confirmation & Final Receipt - " + order.getOrderCode();

            String body = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e8d5ea; border-radius: 8px; padding: 25px; background-color: #ffffff;'>"
                    + "<h2 style='color: #3b0a45; text-align: center; margin-bottom: 20px;'>Payment Completed Successfully! 🧾</h2>"
                    + "<p style='color: #333333; font-size: 15px;'>Dear <b>" + order.getCustomer().getFullName() + "</b>,</p>"
                    + "<p style='color: #555555; line-height: 1.5;'>We have received your final payment for order <b>" + order.getOrderCode() + "</b>. Your order is now fully settled!</p>"

                    + "<div style='background-color: #faf2fc; border-left: 4px solid #6f2c91; padding: 18px; margin: 20px 0; border-radius: 6px;'>"
                    + "<h4 style='margin: 0 0 10px 0; color: #4a005b; text-transform: uppercase; letter-spacing: 0.5px;'>Payment Details</h4>"
                    + "<p style='margin: 6px 0; color: #333333;'><b>Payment Code:</b> " + payment.getPaymentCode() + "</p>"
                    + "<p style='margin: 6px 0; color: #333333;'><b>Order Code:</b> " + order.getOrderCode() + "</p>"
                    + "<p style='margin: 6px 0; color: #333333;'><b>Payment Status:</b> <span style='color: #6f2c91; font-weight: bold;'>COMPLETED (FULL PAID)</span></p>"
                    + "<p style='margin: 6px 0; color: #333333;'><b>Total Paid Amount:</b> <span style='color: #3b0a45; font-weight: bold;'>LKR " + String.format("%,.2f", payment.getPaidAmount()) + "</span></p>"
                    + "<p style='margin: 6px 0; color: #333333;'><b>Remaining Balance:</b> <span style='color: #28a745; font-weight: bold;'>LKR 0.00</span></p>"
                    + "</div>"

                    + "<p style='color: #555555; line-height: 1.5;'>Please find your official payment receipt attached to this email as a PDF document.</p>"
                    + "<br/>"
                    + "<p style='color: #333333; margin: 0;'>Best Regards,<br/><b style='color: #4a005b;'>NexaSupply Distribution Team</b></p>"
                    + "</div>";

            String fileName = "Final_Receipt_" + order.getOrderCode() + ".pdf";

            emailService.sendOrderInvoiceEmail(
                    customer.getEmail(),
                    subject,
                    body,
                    pdfBytes,
                    fileName
            );
            log.info("Final Receipt Email Sent to {}", customer.getEmail());

        } catch (Exception e) {
            log.error("Failed to send final payment receipt email for payment code: {}", payment.getPaymentCode(), e);
        }

    }


}
