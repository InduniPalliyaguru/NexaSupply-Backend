package lk.ijse.NexaSupply.dto;

import lk.ijse.NexaSupply.enumeration.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponseDTO {

    private String driverCode;
    private String driverName;
    private String phone;
    private String licenseNo;
    private DriverStatus status;

}
