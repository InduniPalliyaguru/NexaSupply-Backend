package lk.ijse.NexaSupply.dto.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditLogDTO {

    private long auditLogId;
    private String userEmail;
    private String action;
    private LocalDateTime actionDate;

}
