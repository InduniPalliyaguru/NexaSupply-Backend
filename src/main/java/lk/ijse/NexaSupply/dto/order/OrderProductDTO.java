package lk.ijse.NexaSupply.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductDTO {

    @NotBlank(message = "Product Code is required")
    private String productCode;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

}
