package lk.ijse.NexaSupply.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MonthlySalesDTO {

    private String month;
    private Double totalSales;

    public MonthlySalesDTO(String month, Double totalSales) {
        this.month = month;
        this.totalSales = totalSales;
    }

    public MonthlySalesDTO(Object month, Object totalSales) {
        this.month = month != null ? month.toString() : "";
        this.totalSales = totalSales != null ? ((Number) totalSales).doubleValue() : 0.0;
    }

}
