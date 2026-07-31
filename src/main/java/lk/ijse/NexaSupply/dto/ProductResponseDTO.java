package lk.ijse.NexaSupply.dto;

import lk.ijse.NexaSupply.enumeration.Unit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {

    private long productId;
    private String productCode;
    private String productName;
    private double unitPrice;
    private int availableQty;
    private Unit unit;
    private int minStockLevel;
    private String imageUrl;
    private String categoryCode;
    private String categoryName;

}
