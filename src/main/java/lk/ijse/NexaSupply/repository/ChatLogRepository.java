package lk.ijse.NexaSupply.repository;

import lk.ijse.NexaSupply.entity.ChatLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {

    List<ChatLog> findByUserEmailOrderByCreatedAtAsc(String userEmail);

}
