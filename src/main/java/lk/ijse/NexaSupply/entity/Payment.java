package lk.ijse.NexaSupply.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(nullable = false, unique = true)
    private String paymentCode;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String paymentStatus = "PENDING";

    private LocalDateTime paymentDate = LocalDateTime.now();

    @OneToOne
    @JoinColumn(nullable = false)
    private Order order;
}