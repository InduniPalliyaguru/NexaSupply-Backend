package lk.ijse.NexaSupply.repository;

import lk.ijse.NexaSupply.entity.User;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsUserByEmailAndDataStatus_Active(String email, DataStatus dataStatus);

    @Query ("SELECT COUNT(u) FROM User u")
    long countAllUsers();
}
