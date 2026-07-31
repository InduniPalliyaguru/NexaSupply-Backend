package lk.ijse.NexaSupply.entity;

import jakarta.persistence.*;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import lk.ijse.NexaSupply.enumeration.Unit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long productId;

    @Column(nullable = false, unique = true)
    private String productCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double unitPrice;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    private Unit unit;

    private int minStockLevel;

    @Enumerated(EnumType.STRING)
    private DataStatus dataStatus;

    private String imgUrl;

    @ManyToOne
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderProduct> orderProductList;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RestockHistory> restockHistoryList;

}
