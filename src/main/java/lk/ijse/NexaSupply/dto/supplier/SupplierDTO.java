package lk.ijse.NexaSupply.dto.supplier;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = "Person name is required")
    private String contactPerson;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Address is required")
    private String address;

    public SupplierDTO(String supplierCode, String companyName, String contactPerson, String phone, String email, String address) {
        this.supplierCode = supplierCode;
        this.companyName = companyName;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public SupplierDTO(String companyName, String contactPerson, String phone, String email, String address) {
        this.companyName = companyName;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }
}
