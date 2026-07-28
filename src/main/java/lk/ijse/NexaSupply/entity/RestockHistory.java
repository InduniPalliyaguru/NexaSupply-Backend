package lk.ijse.NexaSupply.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RestockHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long historyId;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Supplier supplier;

    @Column(nullable = false)
    private int qtyAdded;

    @Column(nullable = false)
    private double purchaseUnitPrice;

    private double totalCost;

    private LocalDateTime restockDate;

    private String invoiceNumber;

}
