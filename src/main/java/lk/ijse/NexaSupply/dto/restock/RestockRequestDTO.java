package lk.ijse.NexaSupply.dto.restock;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestockRequestDTO {

    @NotBlank(message = "Supplier code is required")
    private String supplierCode;

    private String invoiceNumber;

    @NotEmpty(message = "Restock items list cannot be empty")
    private List<RestockItemDTO> items;

}
