package lk.ijse.NexaSupply.repository;

import lk.ijse.NexaSupply.entity.Shipment;
import lk.ijse.NexaSupply.enumeration.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    @Query("SELECT s FROM Shipment s WHERE s.trackingNumber = ?1 AND s.dataStatus = 'ACTIVE'")
    Optional<Shipment> findActiveByTrackingNumber(String trackingNumber);

    @Query("SELECT s FROM Shipment s WHERE s.order.orderCode = ?1 AND s.dataStatus = 'ACTIVE'")
    Optional<Shipment> findByOrderCodeActive(String orderCode);

    @Query("SELECT s FROM Shipment s WHERE s.dataStatus = 'ACTIVE'")
    List<Shipment> findAllActiveShipment();

    @Query("SELECT s FROM Shipment s WHERE s.status = ?1 AND s.dataStatus = 'ACTIVE'")
    List<Shipment> findByStatusActive(ShipmentStatus status);

    @Query("SELECT s FROM Shipment s WHERE s.driver.driverCode = ?1 AND s.dataStatus = 'ACTIVE'")
    List<Shipment> findByDriverCodeActive(String driverCode);

    @Query("SELECT COUNT(s) FROM Shipment s")
    long countAllShipments();

}
