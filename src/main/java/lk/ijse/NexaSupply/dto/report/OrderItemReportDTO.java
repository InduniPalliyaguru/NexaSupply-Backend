package lk.ijse.NexaSupply.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemReportDTO {

    private String productName;
    private double unitPrice;
    private int quantity;
    private double subTotal;

}
