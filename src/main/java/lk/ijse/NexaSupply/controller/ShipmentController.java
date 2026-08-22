package lk.ijse.NexaSupply.controller;

import jakarta.validation.Valid;
import lk.ijse.NexaSupply.constant.CommonResponse;
import lk.ijse.NexaSupply.dto.shipment.ShipmentRequestDTO;
import lk.ijse.NexaSupply.dto.shipment.ShipmentResponseDTO;
import lk.ijse.NexaSupply.enumeration.ShipmentStatus;
import lk.ijse.NexaSupply.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse createShipment(@Valid @RequestBody ShipmentRequestDTO dto) {
        ShipmentResponseDTO shipment = shipmentService.createShipment(dto);
        return new CommonResponse(201, shipment, "Shipment created successfully");
    }

    @PutMapping(value = "/{trackingNumber}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse updateShipmentStatus(@PathVariable String trackingNumber, @RequestParam ShipmentStatus status) {
        ShipmentResponseDTO shipment = shipmentService.updateShipmentStatus(trackingNumber, status);
        return new CommonResponse(200, shipment, "Shipment status updated successfully");
    }

    @DeleteMapping(value = "/{trackingNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse deleteShipment(@PathVariable String trackingNumber) {
        shipmentService.deleteShipment(trackingNumber);
        return new CommonResponse(200, "Shipment deleted successfully!");
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse getAllActiveShipments() {
        List<ShipmentResponseDTO> shipments = shipmentService.getALlActiveShipments();
        return new CommonResponse(200, shipments, "Shipment list retrieved successfully!");
    }

    @GetMapping(value = "/{trackingNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RETAILER')")
    public CommonResponse getShipmentByTrackingNumber(@PathVariable String trackingNumber) {
        ShipmentResponseDTO shipment = shipmentService.getShipmentByTrackingNumber(trackingNumber);
        return new CommonResponse(200, shipment, "Shipment details fetched successfully!");
    }

    @GetMapping(value = "/order/{orderCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RETAILER')")
    public CommonResponse getShipmentByOrderCode(@PathVariable String orderCode) {
        ShipmentResponseDTO shipment = shipmentService.getShipmentByOrderCode(orderCode);
        return new CommonResponse(200, shipment, "Shipment details fetched successfully!");
    }

    @GetMapping(value = "/driver/{driverCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse getShipmentsByDriverCode(@PathVariable String driverCode) {
        List<ShipmentResponseDTO> shipments = shipmentService.getShipmentByDriverCode(driverCode);
        return new CommonResponse(200, shipments, "Driver shipment list fetched successfully!");
    }

    @GetMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse getShipmentsByStatus(@RequestParam ShipmentStatus status) {
        List<ShipmentResponseDTO> shipments = shipmentService.getShipmentByStatus(status);
        return new CommonResponse(200, shipments, "Shipments by status fetched successfully!");
    }

}
