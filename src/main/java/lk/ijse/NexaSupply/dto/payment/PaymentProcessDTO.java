package lk.ijse.NexaSupply.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentProcessDTO {

    private String paymentCode;

    @NotNull(message = "Paying amount is required")
    @Positive(message = "Paying amount must be greater than zero")
    private double payingAmount;

}
