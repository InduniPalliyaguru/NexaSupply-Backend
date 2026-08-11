package lk.ijse.NexaSupply.repository;

import lk.ijse.NexaSupply.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentCode(String paymentCode);

    Optional<Payment> findByOrder_OrderCode(String orderCode);

    @Query("SELECT COUNT(p) FROM Payment p")
    long countAllPayments();

}
