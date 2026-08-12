package lk.ijse.NexaSupply.entity;

import jakarta.persistence.*;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import lk.ijse.NexaSupply.enumeration.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long shipmentId;

    @Column(unique = true, nullable = false)
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;

    private LocalDateTime dispatchedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DataStatus dataStatus;

    @OneToOne
    private Order order;

    @ManyToOne
    private Driver driver;

}
