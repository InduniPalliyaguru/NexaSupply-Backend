package lk.ijse.NexaSupply.controller;

import lk.ijse.NexaSupply.constant.CommonResponse;
import lk.ijse.NexaSupply.dto.NotificationDTO;
import lk.ijse.NexaSupply.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public CommonResponse getCurrentUserNotifications() {
        List<NotificationDTO> notifications = notificationService.getCurrentUserNotifications();
        return new CommonResponse(200, notifications, "Notifications fetched successfully!");
    }

    @GetMapping(value = "/unread-count", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public CommonResponse getUnreadCount() {
        long unreadCount = notificationService.getUnreadCountForCurrentUser();
        return new CommonResponse(200, unreadCount, "Unread count fetched successfully!");
    }

    @PutMapping(value = "/{notificationId}/read", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public CommonResponse markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return new CommonResponse(200, "Notification marked as read successfully!");
    }

    @PutMapping(value = "/read-all", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public CommonResponse markAllAsRead() {
        notificationService.markAllAsRead();
        return new CommonResponse(200, "All notifications marked as read successfully!");
    }

    @DeleteMapping(value = "/{notificationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public CommonResponse deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return new CommonResponse(200, "Notification deleted successfully!");
    }

}
