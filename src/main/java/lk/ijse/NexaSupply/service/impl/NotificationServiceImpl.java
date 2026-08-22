package lk.ijse.NexaSupply.service.impl;

import lk.ijse.NexaSupply.dto.system.NotificationDTO;
import lk.ijse.NexaSupply.entity.Notification;
import lk.ijse.NexaSupply.entity.User;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import lk.ijse.NexaSupply.exception.CustomException;
import lk.ijse.NexaSupply.repository.NotificationRepository;
import lk.ijse.NexaSupply.repository.UserRepository;
import lk.ijse.NexaSupply.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public void createNotification(User recipient, String title, String message) {
        log.info("Execute createNotification method");

        if (recipient == null) {
            throw new CustomException(400, "Recipient user cannot be null");
        }

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setDataStatus(DataStatus.ACTIVE);
        notification.setUser(recipient);

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationDTO> getCurrentUserNotifications() {
        log.info("Execute getCurrentUserNotifications method");

        User currentUser = getAuthenticatedUser();
        List<Notification> desc = notificationRepository.findByUserAndDataStatusOrderByCreatedAtDesc(currentUser, DataStatus.ACTIVE);

        List<NotificationDTO> notificationDTOList = new ArrayList<>();
        for (Notification notification : desc) {
            NotificationDTO dto = mapToDTO(notification);
            notificationDTOList.add(dto);
        }
        return notificationDTOList;
    }

    @Override
    public long getUnreadCountForCurrentUser() {
        log.info("Execute getUnreadCountForCurrentUser method");

        User currentUser = getAuthenticatedUser();
        return notificationRepository.countByUserAndIsReadFalseAndDataStatus(currentUser, DataStatus.ACTIVE);
    }

    @Override
    public void markAsRead(long notificationId) {
        log.info("Execute markAsRead method");

        Optional<Notification> notification = notificationRepository.findByNotificationIdAndDataStatus(notificationId, DataStatus.ACTIVE);
        if (notification.isEmpty()) {
            throw new CustomException(400, "Notification not found");
        }
        Notification notify = notification.get();
        notify.setRead(true);
        notificationRepository.save(notify);
    }

    @Override
    public void markAllAsRead() {
        log.info("Execute markAllAsRead method");

        User currentUser = getAuthenticatedUser();
        List<Notification> list = notificationRepository.findByUserAndDataStatusOrderByCreatedAtDesc(currentUser, DataStatus.ACTIVE);
        for (Notification notification : list) {
            if (!notification.isRead()) {
                notification.setRead(true);
            }
        }
        notificationRepository.saveAll(list);
    }

    @Override
    public void deleteNotification(long notificationId) {
        log.info("Execute deleteNotification method");

        Optional<Notification> notification = notificationRepository.findByNotificationIdAndDataStatus(notificationId, DataStatus.ACTIVE);
        if (notification.isEmpty()) {
            throw new CustomException(400, "Notification not found");
        }
        Notification notify = notification.get();
        notify.setDataStatus(DataStatus.INACTIVE);
        notificationRepository.save(notify);
    }

    private User getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(401, "User is not authenticated!");
        }
        String email = authentication.getName();

        Optional<User> activeByEmail = userRepository.findActiveByEmail(email);
        if (activeByEmail.isEmpty()) {
            throw new CustomException(404, "Authenticated user not found!");
        }
        return activeByEmail.get();
    }

    private NotificationDTO mapToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setNotificationId(notification.getNotificationId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());

        if (notification.getUser() != null) {
            dto.setUserCode(notification.getUser().getUserCode());
            dto.setUserName(notification.getUser().getFullName());
        }
        return dto;
    }

}
