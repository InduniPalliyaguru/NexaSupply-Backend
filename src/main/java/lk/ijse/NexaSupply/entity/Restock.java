package lk.ijse.NexaSupply.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Restock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long restockId;

    @Column(nullable = false, unique = true)
    private String restockCode;

    private String invoiceNumber;

    private LocalDateTime restockDate;

    private double totalCose;

    @ManyToOne
    private Supplier supplier;

    @OneToMany(mappedBy = "restock", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RestockDetail> restockDetails;

}
