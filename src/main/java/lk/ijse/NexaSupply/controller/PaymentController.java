package lk.ijse.NexaSupply.controller;

import jakarta.validation.Valid;
import lk.ijse.NexaSupply.constant.CommonResponse;
import lk.ijse.NexaSupply.dto.payment.PaymentDTO;
import lk.ijse.NexaSupply.dto.payment.PaymentProcessDTO;
import lk.ijse.NexaSupply.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PutMapping(value = "/pay", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse processPayment(@Valid @RequestBody PaymentProcessDTO dto) {
        PaymentDTO response = paymentService.processPayment(dto);
        return new CommonResponse(200, response, "Payment processed successful!");
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse getAllPayments() {
        List<PaymentDTO> response = paymentService.getAllPayments();
        return new CommonResponse(200, response, "All payments fetched successful!");
    }

    @GetMapping(value = "/code/{paymentCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse getPaymentByCode(@PathVariable String paymentCode) {
        PaymentDTO response = paymentService.getPaymentByCode(paymentCode);
        return new CommonResponse(200, response, "Payment fetched successful!");
    }

    @GetMapping(value = "/order-code/{orderCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse getPaymentByOrderCode(@PathVariable String orderCode) {
        PaymentDTO response = paymentService.getPaymentByOrderCode(orderCode);
        return new CommonResponse(200, response, "Payment fetched successful!");
    }

}
