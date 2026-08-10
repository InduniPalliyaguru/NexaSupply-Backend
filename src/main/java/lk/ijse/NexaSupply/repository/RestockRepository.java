package lk.ijse.NexaSupply.repository;

import lk.ijse.NexaSupply.entity.Restock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestockRepository extends JpaRepository<Restock, Long> {

    Optional<Restock> findByRestockCode(String restockCode);

    @Query("SELECT r FROM Restock r ORDER BY r.restockDate DESC")
    List<Restock> findAllByOrderByRestockDateDesc();

    @Query("SELECT COUNT(r) FROM Restock r")
    long countAllRestocks();
}
