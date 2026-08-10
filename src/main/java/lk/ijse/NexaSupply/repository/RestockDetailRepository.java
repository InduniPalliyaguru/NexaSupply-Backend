package lk.ijse.NexaSupply.repository;

import lk.ijse.NexaSupply.entity.RestockDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestockDetailRepository extends JpaRepository<RestockDetail, Long> {

    List<RestockDetail> findByRestock_RestockCode(String restockCode);

}
