package lk.ijse.NexaSupply.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardDTO {

    private double totalRevenue;
    private long pendingOrderCount;
    private long lowStockProductsCount;
    private long activeRetailersCount;
    private List<MonthlySalesDTO> monthlySales;

}
