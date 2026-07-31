package lk.ijse.NexaSupply.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lk.ijse.NexaSupply.enumeration.Unit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {

    @NotBlank(message = "Product name is required")
    private String name;

    private String productCode;

    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be greater than zero")
    private String description;

    @NotNull(message = "Available quantity is required")
    @Min(value = 0, message = "Available quantity cannot be negative")
    private double unitPrice;

    @NotNull(message = "Unit type is required")
    private int availableQty;

    @Min(value = 0, message = "Reorder level cannot be negative")
    private Unit unit;

    private int minStockLevel;

    private String imgUrl;

    @NotBlank(message = "Category code is required")
    private String categoryCode;

    public ProductRequestDTO(String name, String description, double unitPrice, int availableQty, Unit unit, int minStockLevel, String imgUrl, String categoryCode) {
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.availableQty = availableQty;
        this.unit = unit;
        this.minStockLevel = minStockLevel;
        this.imgUrl = imgUrl;
        this.categoryCode = categoryCode;
    }
}
