package lk.ijse.NexaSupply.service;

import lk.ijse.NexaSupply.dto.PaymentDTO;
import lk.ijse.NexaSupply.dto.PaymentProcessDTO;
import lk.ijse.NexaSupply.entity.Order;

import java.util.List;

public interface PaymentService {

    void createPendingPaymentForOrder(Order order);

    PaymentDTO processPayment(PaymentProcessDTO dto);

    PaymentDTO getPaymentByCode(String paymentCode);

    List<PaymentDTO> getAllPayments();

    PaymentDTO getPaymentByOrderCode(String orderCode);

}
