package lk.ijse.NexaSupply.dto;

import lk.ijse.NexaSupply.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDataDTO {

    private long userId;
    private String userCode;
    private String role;
    private String token;

}
