package lk.ijse.NexaSupply.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDTO {

    private long supplierId;
    private String supplierCode;

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String contactPerson;
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    private String address;

}
