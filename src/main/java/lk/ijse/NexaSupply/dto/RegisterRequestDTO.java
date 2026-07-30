package lk.ijse.NexaSupply.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {

    private String fullName;
    private String email;
    private String password;
    private String shopName;
    private String phone;
    private String address;
    private double creditLimit;

}
