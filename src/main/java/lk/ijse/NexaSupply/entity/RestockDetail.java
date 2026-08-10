package lk.ijse.NexaSupply.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RestockDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long restockDetailId;

    @ManyToOne
    private Restock restock;

    @ManyToOne
    private Product product;

    @Column(nullable = false)
    private int qtyAdded;

    @Column(nullable = false)
    private double purchaseUnitPrice;

    private double subTotal;

}
