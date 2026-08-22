package lk.ijse.NexaSupply.dto.product;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {

    private long categoryId;
    private String categoryCode;

    @NotBlank(message = "Category name is required")
    private String categoryName;
    private String description;

    public CategoryDTO(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
    }

    public CategoryDTO(String categoryCode, String categoryName, String description) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.description = description;
    }
}
