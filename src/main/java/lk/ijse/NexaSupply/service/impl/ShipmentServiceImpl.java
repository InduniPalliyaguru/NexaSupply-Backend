package lk.ijse.NexaSupply.service.impl;

import lk.ijse.NexaSupply.dto.shipment.ShipmentRequestDTO;
import lk.ijse.NexaSupply.dto.shipment.ShipmentResponseDTO;
import lk.ijse.NexaSupply.entity.Driver;
import lk.ijse.NexaSupply.entity.Order;
import lk.ijse.NexaSupply.entity.Shipment;
import lk.ijse.NexaSupply.entity.User;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import lk.ijse.NexaSupply.enumeration.DriverStatus;
import lk.ijse.NexaSupply.enumeration.OrderStatus;
import lk.ijse.NexaSupply.enumeration.ShipmentStatus;
import lk.ijse.NexaSupply.exception.CustomException;
import lk.ijse.NexaSupply.repository.DriverRepository;
import lk.ijse.NexaSupply.repository.OrderRepository;
import lk.ijse.NexaSupply.repository.ShipmentRepository;
import lk.ijse.NexaSupply.service.AuditLogService;
import lk.ijse.NexaSupply.service.EmailService;
import lk.ijse.NexaSupply.service.NotificationService;
import lk.ijse.NexaSupply.service.ShipmentService;
import lk.ijse.NexaSupply.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;
    private final DriverRepository driverRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Override
    public ShipmentResponseDTO createShipment(ShipmentRequestDTO dto) {
        log.info("Execute create shipment method");

        Optional<Order> orderOptional = orderRepository.findByOrderCode(dto.getOrderCode());
        if (orderOptional.isEmpty()) {
            log.error("Order Not Found with Code: {}", dto.getOrderCode());
            throw new CustomException(404, "Order Not Found with Code: " + dto.getOrderCode());
        }

        Optional<Shipment> existingShipment = shipmentRepository.findByOrderCodeActive(dto.getOrderCode());
        if (existingShipment.isPresent()) {
            log.error("Shipment Already Exists");
            throw new CustomException(400, "Shipment Already Exists for Order Code: " + dto.getOrderCode());
        }

        Optional<Driver> driverOptional = driverRepository.findByDriverCode(dto.getDriverCode());
        if (driverOptional.isEmpty()) {
            log.error("Driver Not Found with Code: {}", dto.getDriverCode());
            throw new CustomException(404, "Driver Not Found with Code: " + dto.getDriverCode());
        }

        Optional<Shipment> previousShipmentOptional = shipmentRepository.findByOrder(orderOptional.get());

        Shipment shipment;
        if (previousShipmentOptional.isPresent()) {
            shipment = previousShipmentOptional.get();
        } else {
            shipment = new Shipment();
        }

        shipment.setTrackingNumber(generateTrackingNumber());
        shipment.setStatus(ShipmentStatus.PENDING);
        shipment.setDataStatus(DataStatus.ACTIVE);
        shipment.setOrder(orderOptional.get());
        shipment.setDriver(driverOptional.get());

        Shipment saved = shipmentRepository.save(shipment);

        if (saved.getOrder() != null && saved.getOrder().getCustomer() != null) {
            User customer = saved.getOrder().getCustomer();
            Driver driver = saved.getDriver();

            notificationService.createNotification(
                    customer,
                    "Shipment Created",
                    "Shipment " + saved.getTrackingNumber() + " has been created for your order " + saved.getOrder().getOrderCode() + "."
            );

            emailService.sendShipmentCreatedEmail(
                    customer.getEmail(),
                    customer.getFullName(),
                    saved.getOrder().getOrderCode(),
                    saved.getTrackingNumber(),
                    driver.getDriverName(),
                    driver.getPhone()
            );
        }

        auditLogService.logAction(SecurityUtils.getCurrentUserEmail(), "CREATED_SHIPMENT | Tracking: " + saved.getTrackingNumber() + " | Order: " + saved.getOrder().getOrderCode());

        return mapToDTO(saved);
    }

    @Override
    public ShipmentResponseDTO updateShipmentStatus(String trackingNumber, ShipmentStatus status) {
        log.info("Execute update shipment status method");

        Optional<Shipment> shipmentOptional = shipmentRepository.findActiveByTrackingNumber(trackingNumber);
        if (shipmentOptional.isEmpty()) {
            log.error("Shipment Not Found with Tracking: {}", trackingNumber);
            throw new CustomException(404, "Shipment Not Found with Tracking Number: " + trackingNumber);
        }

        Shipment shipment = shipmentOptional.get();
        shipment.setStatus(status);

        Order order = shipment.getOrder();
        Driver driver = shipment.getDriver();
        if (order != null && driver != null) {
            if (status == ShipmentStatus.DISPATCHED) {
                shipment.setDispatchedDate(LocalDateTime.now());
                order.setOrderStatus(OrderStatus.DISPATCHED);
                driver.setDriverStatus(DriverStatus.ON_TRIP);

            } else if (status == ShipmentStatus.IN_TRANSIT) {
                order.setOrderStatus(OrderStatus.DISPATCHED);

            } else if (status == ShipmentStatus.DELIVERED) {
                order.setOrderStatus(OrderStatus.DISPATCHED);
                driver.setDriverStatus(DriverStatus.AVAILABLE);

            } else if (status == ShipmentStatus.CANCELLED) {
                order.setOrderStatus(OrderStatus.APPROVED);
            }
            orderRepository.save(order);
        }
        Shipment updated = shipmentRepository.save(shipment);

        if (shipment.getOrder() != null && shipment.getOrder().getCustomer() != null) {
            User customer = shipment.getOrder().getCustomer();
            notificationService.createNotification(
                    customer,
                    "Shipment Status Updated",
                    "Your shipment " + trackingNumber + " status is now " + status + "."
            );

            emailService.sendShipmentStatusUpdateEmail(
                    customer.getEmail(),
                    customer.getFullName(),
                    shipment.getOrder().getOrderCode(),
                    trackingNumber,
                    driver != null ? driver.getDriverName() : "N/A",
                    driver != null ? driver.getPhone() : "N/A",
                    status.name()
            );
        }

        auditLogService.logAction(SecurityUtils.getCurrentUserEmail(), "UPDATED_SHIPMENT_STATUS | Tracking: " + trackingNumber + " | Status: " + status);
        return mapToDTO(updated);
    }

    @Override
    public ShipmentResponseDTO getShipmentByTrackingNumber(String trackingNumber) {
        log.info("Execute getShipmentByTrackingNumber method");

        Optional<Shipment> shipmentOptional = shipmentRepository.findActiveByTrackingNumber(trackingNumber);
        if (shipmentOptional.isEmpty()) {
            log.error("Shipment Not Found with Tracking : {}", trackingNumber);
            throw new CustomException(404, "Shipment Not Found with Tracking Number: " + trackingNumber);
        }
        Shipment shipment = shipmentOptional.get();
        return mapToDTO(shipment);
    }

    @Override
    public ShipmentResponseDTO getShipmentByOrderCode(String orderCode) {
        log.info("Execute getShipmentByOrderCode method");

        Optional<Shipment> shipmentOptional = shipmentRepository.findByOrderCodeActive(orderCode);
        if (shipmentOptional.isEmpty()) {
            log.error("Shipment Not Found for Order Code: {}", orderCode);
            throw new CustomException(404, "Shipment Not Found for Order Code: " + orderCode);
        }
        Shipment shipment = shipmentOptional.get();
        return mapToDTO(shipment);
    }

    @Override
    public List<ShipmentResponseDTO> getALlActiveShipments() {
        log.info("Execute getAllActiveShipments method");

        List<Shipment> all = shipmentRepository.findAllActiveShipment();

        List<ShipmentResponseDTO> dtoList = new ArrayList<>();
        for (Shipment shipment : all) {
            ShipmentResponseDTO mapped = mapToDTO(shipment);
            dtoList.add(mapped);
        }
        return dtoList;
    }

    @Override
    public List<ShipmentResponseDTO> getShipmentByStatus(ShipmentStatus status) {
        log.info("Execute getShipmentsByStatus method");

        List<Shipment> shipments = shipmentRepository.findByStatusActive(status);

        List<ShipmentResponseDTO> dtoList = new ArrayList<>();
        for (Shipment shipment : shipments) {
            ShipmentResponseDTO mapped = mapToDTO(shipment);
            dtoList.add(mapped);
        }
        return dtoList;
    }

    @Override
    public List<ShipmentResponseDTO> getShipmentByDriverCode(String driverCode) {
        log.info("Execute getShipmentsByDriverCode method");

        List<Shipment> shipments = shipmentRepository.findByDriverCodeActive(driverCode);

        List<ShipmentResponseDTO> dtoList = new ArrayList<>();
        for (Shipment shipment : shipments) {
            ShipmentResponseDTO mapped = mapToDTO(shipment);
            dtoList.add(mapped);
        }
        return dtoList;
    }

    @Override
    public void deleteShipment(String trackingNumber) {
        log.info("Execute delete shipment method");

        Optional<Shipment> shipmentOptional = shipmentRepository.findActiveByTrackingNumber(trackingNumber);
        if (shipmentOptional.isEmpty()) {
            log.error("Shipment Not Found with Tracking Number: {}", trackingNumber);
            throw new CustomException(404, "Shipment Not Found with Tracking Number: " + trackingNumber);
        }
        Shipment shipment = shipmentOptional.get();
        shipment.setDataStatus(DataStatus.INACTIVE);
        shipmentRepository.save(shipment);

        auditLogService.logAction(SecurityUtils.getCurrentUserEmail(), "DELETED_SHIPMENT | Tracking: " + trackingNumber);
    }

    private String generateTrackingNumber() {
        int year = Year.now().getValue();
        long count = shipmentRepository.countAllShipments() + 1;
        return String.format("TRK-%d-%04d", year, count);
    }

    private ShipmentResponseDTO mapToDTO(Shipment shipment) {
        ShipmentResponseDTO dto = new ShipmentResponseDTO();
        dto.setShipmentId(shipment.getShipmentId());
        dto.setTrackingNumber(shipment.getTrackingNumber());
        dto.setStatus(shipment.getStatus());
        dto.setDispatchedDate(shipment.getDispatchedDate());

        if (shipment.getOrder() != null) {
            dto.setOrderCode(shipment.getOrder().getOrderCode());
        }

        if (shipment.getDriver() != null) {
            dto.setDriverCode(shipment.getDriver().getDriverCode());
            dto.setDriverName(shipment.getDriver().getDriverName());
        }
        return dto;
    }

}
