package lk.ijse.NexaSupply.service;

import lk.ijse.NexaSupply.dto.ShipmentRequestDTO;
import lk.ijse.NexaSupply.dto.ShipmentResponseDTO;
import lk.ijse.NexaSupply.enumeration.ShipmentStatus;

import java.util.List;

public interface ShipmentService {

    ShipmentResponseDTO createShipment(ShipmentRequestDTO dto);

    ShipmentResponseDTO updateShipmentStatus(String trackingNumber, ShipmentStatus status);

    ShipmentResponseDTO getShipmentByTrackingNumber(String trackingNumber);

    ShipmentResponseDTO getShipmentByOrderCode(String orderCode);

    List<ShipmentResponseDTO> getALlActiveShipments();

    List<ShipmentResponseDTO> getShipmentByStatus(ShipmentStatus status);

    List<ShipmentResponseDTO> getShipmentByDriverCode(String driverCode);

    void deleteShipment(String trackingNumber);

}
