package lk.ijse.NexaSupply.service.impl;

import lk.ijse.NexaSupply.dto.auth.UserResponseDTO;
import lk.ijse.NexaSupply.dto.dashboard.AdminDashboardDTO;
import lk.ijse.NexaSupply.dto.product.ProductResponseDTO;
import lk.ijse.NexaSupply.dto.report.OrderInvoiceReportDTO;
import lk.ijse.NexaSupply.dto.report.OrderItemReportDTO;
import lk.ijse.NexaSupply.dto.restock.RestockResponseDTO;
import lk.ijse.NexaSupply.dto.shipment.ShipmentResponseDTO;
import lk.ijse.NexaSupply.entity.*;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import lk.ijse.NexaSupply.enumeration.DriverStatus;
import lk.ijse.NexaSupply.repository.*;
import lk.ijse.NexaSupply.service.AiToolsService;
import lk.ijse.NexaSupply.service.DashboardService;
import lk.ijse.NexaSupply.service.RestockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiToolServiceImpl implements AiToolsService {

    private final DashboardService dashboardService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CreditLedgerRepository creditLedgerRepository;
    private final OrderRepository orderRepository;
    private final ShipmentRepository shipmentRepository;
    private final RestockService restockService;
    private final DriverRepository driverRepository;
    private final CategoryRepository categoryRepository;
    private final AuditLogRepository auditLogRepository;
    private final NotificationRepository notificationRepository;
    private final SupplierRepository supplierRepository;

    @Override
    public AdminDashboardDTO getSystemOverviewData() {
        log.info("AI Tool Executing: getSystemOverviewData");
        try {
            return dashboardService.getAdminDashboardData();
        } catch (Exception e) {
            log.error("Error fetching admin dashboard data for AI: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public ProductResponseDTO getProductInformation(String productCodeOrName) {
        log.info("AI Tool Executing: getProductInformation for query: {}", productCodeOrName);

        Optional<Product> productOpt = productRepository.findActiveByProductCode(productCodeOrName);

        if (productOpt.isEmpty()) {
            List<Product> activeProducts = productRepository.findAllActiveProducts();
            for (Product p : activeProducts) {
                if (p.getName().equalsIgnoreCase(productCodeOrName) || p.getName().toLowerCase().contains(productCodeOrName.toLowerCase())) {
                    productOpt = Optional.of(p);
                    break;
                }
            }
        }
        if (productOpt.isPresent()) {
            Product p = productOpt.get();
            ProductResponseDTO dto = new ProductResponseDTO();
            dto.setProductCode(p.getProductCode());
            dto.setProductName(p.getName());
            dto.setUnitPrice(p.getUnitPrice());
            dto.setAvailableQty(p.getQuantity());
            dto.setCategoryName(p.getCategory() != null ? p.getCategory().getName() : "N/A");
            return dto;
        }
        return null;
    }

    @Override
    public Map<String, Object> getRetailerCreditAndLedgerDetails(String userCodeOrEmail) {
        log.info("AI Tool Executing: getRetailerCreditAndLedgerDetails for: {}", userCodeOrEmail);
        Map<String, Object> response = new HashMap<>();

        Optional<User> userOpt = userRepository.findByUserCodeAndDataStatus(userCodeOrEmail, DataStatus.ACTIVE);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findActiveByEmail(userCodeOrEmail);
        }

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            response.put("userCode", user.getUserCode());
            response.put("fullName", user.getFullName());
            response.put("shopName", user.getShopName());
            response.put("creditLimit", user.getCreditLimit());

            List<CreditLedger> ledgers = creditLedgerRepository.findByCustomer_UserCode(user.getUserCode());
            List<Map<String, Object>> ledgerList = new ArrayList<>();

            for (CreditLedger cl : ledgers) {
                Map<String, Object> listMap = new HashMap<>();
                listMap.put("referenceCode", cl.getReferenceCode());
                listMap.put("amount", cl.getAmount());
                listMap.put("balanceAfter", cl.getBalanceAfter());
                listMap.put("ledgerType", cl.getLedgerType().name());
                listMap.put("description", cl.getDescription());
                listMap.put("createdAt", cl.getTransactionDate());
                ledgerList.add(listMap);
            }

            response.put("ledgerHistory", ledgerList);
            return response;
        }

        return Collections.emptyMap();
    }

    @Override
    public OrderInvoiceReportDTO getOrderDetailsAndStatus(String orderCode) {
        log.info("AI Tool Executing: getOrderDetailsAndStatus for: {}", orderCode);

        Optional<Order> orderOpt = orderRepository.findByOrderCode(orderCode);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            OrderInvoiceReportDTO report = new OrderInvoiceReportDTO();
            report.setOrderCode(order.getOrderCode());
            report.setOrderDate(order.getOrderDate() != null ? order.getOrderDate().toString() : "N/A");
            report.setOrderStatus(order.getOrderStatus().name());

            if (order.getCustomer() != null) {
                report.setCustomerName(order.getCustomer().getFullName());
                report.setShopName(order.getCustomer().getShopName());
                report.setCustomerEmail(order.getCustomer().getEmail());
                report.setCustomerPhone(order.getCustomer().getPhone());
            }

            if (order.getPayment() != null) {
                report.setPayCode(order.getPayment().getPaymentCode());
                report.setPaymentStatus(order.getPayment().getPaymentStatus().name());
                report.setGrandTotal(order.getPayment().getTotalAmount());
                report.setPaidAmount(order.getPayment().getPaidAmount());
                report.setBalanceAmount(order.getPayment().getBalanceAmount());
            }

            List<OrderItemReportDTO> itemList = new ArrayList<>();
            if (order.getOrderProductList() != null) {
                for (OrderProduct od : order.getOrderProductList()) {
                    OrderItemReportDTO itemDTO = new lk.ijse.NexaSupply.dto.report.OrderItemReportDTO();
                    itemDTO.setProductName(od.getProduct() != null ? od.getProduct().getName() : "N/A");
                    itemDTO.setQuantity(od.getQuantity());
                    itemDTO.setUnitPrice(od.getUnitPrice());
                    itemDTO.setSubTotal(od.getQuantity() * od.getUnitPrice());
                    itemList.add(itemDTO);
                }
            }
            report.setItems(itemList);
            return report;
        }

        return null;
    }

    @Override
    public ShipmentResponseDTO getShipmentTrackingDetails(String trackingNumberOrOrderCode) {
        log.info("AI Tool Executing: getShipmentTrackingDetails for: {}", trackingNumberOrOrderCode);

        Optional<Shipment> shipmentOpt = shipmentRepository.findActiveByTrackingNumber(trackingNumberOrOrderCode);
        if (shipmentOpt.isEmpty()) {
            shipmentOpt = shipmentRepository.findByOrderCodeActive(trackingNumberOrOrderCode);
        }

        if (shipmentOpt.isPresent()) {
            Shipment s = shipmentOpt.get();
            ShipmentResponseDTO dto = new ShipmentResponseDTO();
            dto.setShipmentId(s.getShipmentId());
            dto.setTrackingNumber(s.getTrackingNumber());
            dto.setStatus(s.getStatus());
            dto.setDispatchedDate(s.getDispatchedDate());

            if (s.getOrder() != null) {
                dto.setOrderCode(s.getOrder().getOrderCode());
            }

            if (s.getDriver() != null) {
                dto.setDriverCode(s.getDriver().getDriverCode());
                dto.setDriverName(s.getDriver().getDriverName());
            }
            return dto;
        }

        return null;
    }

    @Override
    public List<RestockResponseDTO> getSupplierAndRestockHistory(String supplierCodeOrQuery) {
        log.info("AI Tool Executing: getSupplierAndRestockHistory for: {}", supplierCodeOrQuery);

        List<RestockResponseDTO> allHistory = restockService.getAllRestockHistory();
        if (supplierCodeOrQuery == null || supplierCodeOrQuery.trim().isEmpty()) {
            return allHistory;
        }

        List<RestockResponseDTO> filtered = new ArrayList<>();
        for (RestockResponseDTO r : allHistory) {
            if (r.getSupplierName().equalsIgnoreCase(supplierCodeOrQuery) ||
                    r.getRestockCode().equalsIgnoreCase(supplierCodeOrQuery) ||
                    r.getSupplierName().toLowerCase().contains(supplierCodeOrQuery.toLowerCase())) {
                filtered.add(r);
            }
        }

        return filtered;
    }

    @Override
    public List<DriverStatus> getDriverAvailabilityStatus(DriverStatus status) {
        log.info("AI Tool Executing: getDriverAvailabilityStatus for status: {}", status);

        List<Driver> drivers = driverRepository.findByDriverStatus(status);
        List<DriverStatus> statusList = new ArrayList<>();

        for (Driver d : drivers) {
            statusList.add(d.getDriverStatus());
        }

        return statusList;
    }

    @Override
    public UserResponseDTO getUserProfileDetails(String emailOrUserCode) {
        log.info("AI Tool Executing: getUserProfileDetails for: {}", emailOrUserCode);

        Optional<User> userOpt = userRepository.findByUserCodeAndDataStatus(emailOrUserCode, DataStatus.ACTIVE);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findActiveByEmail(emailOrUserCode);
        }

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            UserResponseDTO dto = new UserResponseDTO();
            dto.setId(user.getUserId());
            dto.setUserCode(user.getUserCode());
            dto.setFullName(user.getFullName());
            dto.setEmail(user.getEmail());
            dto.setShopName(user.getShopName());
            dto.setPhone(user.getPhone());
            dto.setAddress(user.getAddress());
            dto.setRole(user.getRole());
            dto.setProfileStatus(user.getProfileStatus());
            dto.setCreditLimit(user.getCreditLimit());
            return dto;
        }
        return null;
    }

    @Override
    public List<String> getCategoryList() {
        log.info("AI Tool Executing: getCategoryList");

        List<Category> categories = categoryRepository.findAllActiveCategories();
        List<String> categoryNames = new ArrayList<>();

        for (Category c : categories) {
            categoryNames.add(c.getName());
        }
        return categoryNames;
    }

    @Override
    public List<String> getAuditLogSummary(String userEmail) {
        log.info("AI Tool Executing: getAuditLogSummary for email: {}", userEmail);

        List<AuditLog> logs;
        if (userEmail != null && !userEmail.trim().isEmpty()) {
            logs = auditLogRepository.findAllByOrderByActionDateDesc();
        } else {
            logs = auditLogRepository.findAll();
        }

        List<String> summary = new ArrayList<>();
        for (AuditLog al : logs) {
            summary.add("[" + al.getActionDate() + "] " + al.getUserEmail() + ": " + al.getAction());
        }
        return summary;
    }

    @Override
    public List<String> getNotificationHistory(String userEmail) {
        log.info("AI Tool Executing: getNotificationHistory for email: {}", userEmail);

        Optional<User> userOpt = userRepository.findActiveByEmail(userEmail);
        if (userOpt.isPresent()) {
            List<Notification> notifications = notificationRepository.findByUserAndDataStatusOrderByCreatedAtDesc(userOpt.get(), DataStatus.ACTIVE);
            List<String> notifList = new ArrayList<>();

            for (Notification n : notifications) {
                notifList.add("[" + n.getCreatedAt() + "] " + n.getTitle() + " - " + n.getMessage());
            }

            return notifList;
        }
        return Collections.emptyList();
    }

    @Override
    public Map<String, Object> searchSystemData(String searchQuery) {
        log.info("AI Tool Executing: searchSystemData for: {}", searchQuery);

        Map<String, Object> resultMap = new HashMap<>();

        List<Product> products = productRepository.findAllActiveProducts();
        List<Map<String, Object>> matchedProducts = new ArrayList<>();
        for (Product p : products) {
            if (p.getName().toLowerCase().contains(searchQuery.toLowerCase()) || p.getProductCode().equalsIgnoreCase(searchQuery)) {
                Map<String, Object> pMap = new HashMap<>();
                pMap.put("productCode", p.getProductCode());
                pMap.put("name", p.getName());
                pMap.put("price", p.getUnitPrice());
                pMap.put("qty", p.getQuantity());
                matchedProducts.add(pMap);
            }
        }
        resultMap.put("products", matchedProducts);

        List<Supplier> suppliers = supplierRepository.searchSuppliers(searchQuery);
        List<Map<String, Object>> matchedSuppliers = new ArrayList<>();
        for (Supplier s : suppliers) {
            Map<String, Object> sMap = new HashMap<>();
            sMap.put("supplierCode", s.getSupplierCode());
            sMap.put("companyName", s.getCompanyName());
            sMap.put("phone", s.getPhone());
            matchedSuppliers.add(sMap);
        }
        resultMap.put("suppliers", matchedSuppliers);

        Optional<Order> orderOpt = orderRepository.findByOrderCode(searchQuery);
        if (orderOpt.isPresent()) {
            Order o = orderOpt.get();
            Map<String, Object> oMap = new HashMap<>();
            oMap.put("orderCode", o.getOrderCode());
            oMap.put("status", o.getOrderStatus().name());
            resultMap.put("order", oMap);
        }
        return resultMap;
    }
}
