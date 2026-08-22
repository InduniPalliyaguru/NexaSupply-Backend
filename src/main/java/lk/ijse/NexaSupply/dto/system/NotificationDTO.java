package lk.ijse.NexaSupply.dto.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {

    private long notificationId;
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
    private String userCode;
    private String userName;

}
