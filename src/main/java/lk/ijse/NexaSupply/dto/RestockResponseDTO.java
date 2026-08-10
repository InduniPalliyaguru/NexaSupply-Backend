package lk.ijse.NexaSupply.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestockResponseDTO {

    private String restockCode;
    public String supplierName;
    private String invoiceNumber;
    private LocalDateTime restockDate;
    private double totalCost;
    private List<RestockDetailResponseDTO> details;

}
