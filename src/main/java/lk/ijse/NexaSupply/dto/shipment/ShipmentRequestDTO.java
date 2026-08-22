package lk.ijse.NexaSupply.dto.shipment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentRequestDTO {

    @NotBlank(message = "Order code is required")
    private String orderCode;

    @NotBlank(message = "Driver code is required")
    private String driverCode;

}
