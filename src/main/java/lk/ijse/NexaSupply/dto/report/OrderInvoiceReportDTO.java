package lk.ijse.NexaSupply.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInvoiceReportDTO {

    private String orderCode;
    private String orderDate;
    private String orderStatus;
    private String customerName;
    private String shopName;
    private String customerEmail;
    private String customerPhone;
    private String payCode;
    private String paymentStatus;
    private double grandTotal;
    private double paidAmount;
    private double balanceAmount;
    private List<OrderItemReportDTO> items;

}
