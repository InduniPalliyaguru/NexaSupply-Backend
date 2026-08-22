package lk.ijse.NexaSupply.dto.payment;

import lk.ijse.NexaSupply.enumeration.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {

    private long paymentId;
    private String paymentCode;
    private double totalAmount;
    private double paidAmount;
    private double balanceAmount;
    private PaymentStatus paymentStatus;
    private LocalDateTime paymentDate;
    private String OrderCode;
    private String customerName;

}
