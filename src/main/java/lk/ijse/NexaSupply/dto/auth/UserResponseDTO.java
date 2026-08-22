package lk.ijse.NexaSupply.dto.auth;

import lk.ijse.NexaSupply.enumeration.ProfileStatus;
import lk.ijse.NexaSupply.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private long id;
    private String userCode;
    private String fullName;
    private String email;
    private String shopName;
    private String phone;
    private String address;
    private Role role;
    private ProfileStatus profileStatus;
    private Double creditLimit;

    public UserResponseDTO(long id, String userCode, String fullName, String email, String phone, String address, Role role, ProfileStatus profileStatus) {
        this.id = id;
        this.userCode = userCode;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.profileStatus = profileStatus;
    }

    public UserResponseDTO(long id, String userCode, String fullName, String email, String shopName, String phone, String address) {
        this.id = id;
        this.userCode = userCode;
        this.fullName = fullName;
        this.email = email;
        this.shopName = shopName;
        this.phone = phone;
        this.address = address;
    }
}
