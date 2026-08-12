package lk.ijse.NexaSupply.repository;

import lk.ijse.NexaSupply.entity.Notification;
import lk.ijse.NexaSupply.entity.User;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserAndDataStatusOrderByCreatedAtDesc(User user, DataStatus dataStatus);

    long countByUserAndIsReadFalseAndDataStatus(User user, DataStatus dataStatus);

    Optional<Notification> findByNotificationIdAndDataStatus(long notificationId, DataStatus dataStatus);

}
