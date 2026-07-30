package lk.ijse.NexaSupply.repository;

import lk.ijse.NexaSupply.entity.User;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import lk.ijse.NexaSupply.enumeration.ProfileStatus;
import lk.ijse.NexaSupply.enumeration.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmailAndDataStatus_Active(String email, DataStatus dataStatus);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = ?1 AND u.dataStatus = 'ACTIVE'")
    boolean existsActiveByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email = ?1 AND u.dataStatus = 'ACTIVE'")
    Optional<User> findActiveByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.dataStatus = 'ACTIVE'")
    List<User> findAllActiveUsers();

    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();

    Optional<User> findByUserCodeAndDataStatus(String userCode, DataStatus dataStatus);

    List<User> findByProfileStatusAndDataStatus(ProfileStatus status, DataStatus dataStatus);

    List<User> findByRoleAndDataStatus(Role role, DataStatus dataStatus);

    List<User> findByUserCodeContainingIgnoreCaseAndDataStatus(String userCode, DataStatus dataStatus);

    List<User> findByEmailContainingIgnoreCaseAndDataStatus(String email, DataStatus dataStatus);

}
