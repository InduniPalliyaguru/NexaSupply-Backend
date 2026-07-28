package lk.ijse.NexaSupply.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderProductId;

    @ManyToOne
    private Order order;

    @ManyToOne
    private Product product;

    private int quantity;

    private double unitPrice;

}
