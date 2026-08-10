package lk.ijse.NexaSupply.entity;

import jakarta.persistence.*;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long supplierId;

    @Column(nullable = false, unique = true)
    private String supplierCode;

    @Column(nullable = false)
    private String companyName;

    private String contactPerson;

    private String phone;

    private String email;

    private String address;

    @Enumerated(EnumType.STRING)
    private DataStatus dataStatus;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Restock> restockList;

}
