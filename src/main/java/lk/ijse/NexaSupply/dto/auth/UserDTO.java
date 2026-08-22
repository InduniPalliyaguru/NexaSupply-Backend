package lk.ijse.NexaSupply.dto.auth;

import lk.ijse.NexaSupply.enumeration.ProfileStatus;
import lk.ijse.NexaSupply.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private long userId;
    private String userCode;
    private String fullName;
    private String email;
    private String password;
    private Role role;
    private ProfileStatus profileStatus;

}
