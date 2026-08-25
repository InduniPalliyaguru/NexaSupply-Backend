package lk.ijse.NexaSupply.service;

import lk.ijse.NexaSupply.dto.auth.UserResponseDTO;
import lk.ijse.NexaSupply.dto.dashboard.AdminDashboardDTO;
import lk.ijse.NexaSupply.dto.product.ProductResponseDTO;
import lk.ijse.NexaSupply.dto.report.OrderInvoiceReportDTO;
import lk.ijse.NexaSupply.dto.restock.RestockResponseDTO;
import lk.ijse.NexaSupply.dto.shipment.ShipmentResponseDTO;
import lk.ijse.NexaSupply.enumeration.DriverStatus;

import java.util.List;
import java.util.Map;

public interface AiToolsService {

    AdminDashboardDTO getSystemOverviewData();

    ProductResponseDTO getProductInformation(String productCodeOrName);

    Map<String, Object> getRetailerCreditAndLedgerDetails(String userCodeOrEmail);

    OrderInvoiceReportDTO getOrderDetailsAndStatus(String orderCode);

    ShipmentResponseDTO getShipmentTrackingDetails(String trackingNumberOrOrderCode);

    List<RestockResponseDTO> getSupplierAndRestockHistory(String supplierCodeOrQuery);

    List<DriverStatus> getDriverAvailabilityStatus(DriverStatus status);

    UserResponseDTO getUserProfileDetails(String emailOrUserCode);

    List<String> getCategoryList();

    List<String> getAuditLogSummary(String userEmail);

    List<String> getNotificationHistory(String userEmail);

    Map<String, Object> searchSystemData(String searchQuery);

    List<ProductResponseDTO> getAllProductsList();

}
