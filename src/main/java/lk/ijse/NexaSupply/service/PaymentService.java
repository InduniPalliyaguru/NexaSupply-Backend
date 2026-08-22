package lk.ijse.NexaSupply.service;

import lk.ijse.NexaSupply.dto.payment.PaymentDTO;
import lk.ijse.NexaSupply.dto.payment.PaymentProcessDTO;
import lk.ijse.NexaSupply.entity.Order;
import lk.ijse.NexaSupply.entity.Payment;

import java.util.List;

public interface PaymentService {

    Payment createPendingPaymentForOrder(Order order);

    PaymentDTO processPayment(PaymentProcessDTO dto);

    PaymentDTO getPaymentByCode(String paymentCode);

    List<PaymentDTO> getAllPayments();

    PaymentDTO getPaymentByOrderCode(String orderCode);

}
