package lk.ijse.NexaSupply.repository;

import lk.ijse.NexaSupply.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp> findTopByEmailAndOtpCodeAndIsUsedFalseOrderByExpiryTimeDesc(String email, String otpCode);

}
