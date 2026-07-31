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
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long categoryId;

    @Column(nullable = false, unique = true)
    private String categoryCode;

    @Column(nullable = false, unique = true)
    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private DataStatus dataStatus;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> productList;

}
