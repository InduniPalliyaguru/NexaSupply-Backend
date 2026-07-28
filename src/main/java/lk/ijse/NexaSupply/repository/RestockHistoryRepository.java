package lk.ijse.NexaSupply.repository;

import lk.ijse.NexaSupply.entity.RestockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestockHistoryRepository extends JpaRepository<RestockHistory, Long> {
}
