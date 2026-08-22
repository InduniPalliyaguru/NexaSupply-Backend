package lk.ijse.NexaSupply.service;

import lk.ijse.NexaSupply.dto.system.NotificationDTO;
import lk.ijse.NexaSupply.entity.User;

import java.util.List;

public interface NotificationService {

    void createNotification(User recipient, String title, String message);

    List<NotificationDTO> getCurrentUserNotifications();

    long getUnreadCountForCurrentUser();

    void markAsRead(long notificationId);

    void markAllAsRead();

    void deleteNotification(long notificationId);
}
