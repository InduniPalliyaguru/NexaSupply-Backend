package lk.ijse.NexaSupply.dto.restock;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestockItemDTO {

    @NotBlank(message = "Product Code is required")
    private String productCode;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int qtyAdded;

    @Min(value = 0, message = "Purchase unit price cannot be negative")
    private double purchaseUnitPrice;

}
