package lk.ijse.NexaSupply.repository;

import lk.ijse.NexaSupply.entity.CreditLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditLedgerRepository extends JpaRepository<CreditLedger, Long> {

    List<CreditLedger> findByCustomer_UserCode(String userCode);

    List<CreditLedger> findByReferenceCode(String referenceCode);

    @Query("SELECT COUNT(c) FROM CreditLedger c")
    long countAllLedgers();

}
