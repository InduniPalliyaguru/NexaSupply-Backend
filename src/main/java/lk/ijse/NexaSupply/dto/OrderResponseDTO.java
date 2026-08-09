package lk.ijse.NexaSupply.dto;

import lk.ijse.NexaSupply.enumeration.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private long orderId;
    private String orderCode;
    private LocalDateTime orderDate;
    private double totalPrice;
    private OrderStatus orderStatus;
    private String customerEmail;
    private List<OrderProductDTO> orderItems;

}
