package lk.ijse.NexaSupply.repository;

import lk.ijse.NexaSupply.entity.Driver;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import lk.ijse.NexaSupply.enumeration.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query("SELECT d FROM Driver d WHERE d.driverCode = ?1 AND d.dataStatus = 'ACTIVE'")
    Optional<Driver> findByDriverCode(String driverCode);

    boolean existsByDriverCodeAndDataStatus(String driverCode, DataStatus dataStatus);

    boolean existsByPhoneAndDataStatus(String phone, DataStatus dataStatus);

    boolean existsByLicenseNoAndDataStatus(String licenseNo, DataStatus dataStatus);

    @Query("SELECT d FROM Driver d WHERE d.dataStatus = 'ACTIVE'")
    List<Driver> findAllActiveDrivers();

    @Query("SELECT d FROM Driver d WHERE d.driverStatus = ?1 AND d.dataStatus = 'ACTIVE'")
    List<Driver> findByDriverStatus(DriverStatus status);

    @Query("SELECT COUNT(d) FROM Driver d")
    long countAllDrivers();

}
