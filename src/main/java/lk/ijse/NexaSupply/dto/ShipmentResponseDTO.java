package lk.ijse.NexaSupply.dto;

import lk.ijse.NexaSupply.enumeration.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentResponseDTO {

    private long shipmentId;
    private String trackingNumber;
    private ShipmentStatus status;
    private LocalDateTime dispatchedDate;
    private String orderCode;
    private String driverCode;
    private String driverName;

}
