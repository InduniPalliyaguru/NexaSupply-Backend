package lk.ijse.NexaSupply.entity;

import jakarta.persistence.*;
import lk.ijse.NexaSupply.enumeration.LedgerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CreditLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ledgerId;

    @Column(nullable = false, unique = true)
    private String ledgerCode;

    private String referenceCode;

    @Column(nullable = false)
    private double amount;

    private double balanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LedgerType ledgerType;

    private String description;

    private LocalDateTime transactionDate = LocalDateTime.now();

    @ManyToOne
    private User customer;

}
