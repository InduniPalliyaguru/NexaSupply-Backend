package lk.ijse.NexaSupply.entity;

import jakarta.persistence.*;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import lk.ijse.NexaSupply.enumeration.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long driverId;

    @Column(nullable = false, unique = true)
    private String driverCode;

    @Column(nullable = false)
    private String driverName;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false, unique = true)
    private String licenseNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverStatus driverStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DataStatus dataStatus;

    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Shipment> shipments;

}
