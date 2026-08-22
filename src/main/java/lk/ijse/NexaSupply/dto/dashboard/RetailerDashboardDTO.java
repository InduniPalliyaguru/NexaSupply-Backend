package lk.ijse.NexaSupply.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetailerDashboardDTO {

    private double availableCreditLimit;
    private long pendingOrdersCount;
    private long totalOrdersCount;
    private double totalSpentAmount;
    private List<RecentOrderDTO> recentOrders;

}
