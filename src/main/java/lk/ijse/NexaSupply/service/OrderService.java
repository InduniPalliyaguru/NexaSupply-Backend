package lk.ijse.NexaSupply.service;

import lk.ijse.NexaSupply.dto.OrderRequestDTO;
import lk.ijse.NexaSupply.dto.OrderResponseDTO;
import lk.ijse.NexaSupply.enumeration.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderResponseDTO placeOrder(OrderRequestDTO requestDTO);

    OrderResponseDTO updateOrderStatus(String orderCode, OrderStatus status);

    OrderResponseDTO getOrderByCode(String orderCode);

    List<OrderResponseDTO> getMyOrders();

    List<OrderResponseDTO> getAllOrders();

}
