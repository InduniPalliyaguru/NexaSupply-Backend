package lk.ijse.NexaSupply.dto.payment;

import lk.ijse.NexaSupply.enumeration.LedgerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditLedgerDTO {

    private long ledgerId;
    private String ledgerCode;
    private String referenceCode;
    private double amount;
    private double balanceAfter;
    private LedgerType ledgerType;
    private String description;
    private LocalDateTime transactionDate;
    private String userCode;
    private String customerName;

    public CreditLedgerDTO(String referenceCode, double amount, double balanceAfter, LedgerType ledgerType, String description, String userCode) {
        this.referenceCode = referenceCode;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.ledgerType = ledgerType;
        this.description = description;
        this.userCode = userCode;
    }
}
