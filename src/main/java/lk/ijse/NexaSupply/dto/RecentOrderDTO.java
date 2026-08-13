package lk.ijse.NexaSupply.dto;

import lk.ijse.NexaSupply.enumeration.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentOrderDTO {

    private String orderCode;
    private LocalDateTime orderDate;
    private double totalPrice;
    private OrderStatus orderStatus;

}
