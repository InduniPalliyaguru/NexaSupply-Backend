package lk.ijse.NexaSupply.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestockDetailResponseDTO {

    private String productCode;
    private String productName;
    private int qtyAdded;
    private double purchaseUnitPrice;
    private double subTotal;

}
