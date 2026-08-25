package lk.ijse.NexaSupply.service.impl;

import lk.ijse.NexaSupply.dto.auth.UserResponseDTO;
import lk.ijse.NexaSupply.dto.chatbot.ChatRequestDTO;
import lk.ijse.NexaSupply.dto.chatbot.ChatResponseDTO;
import lk.ijse.NexaSupply.dto.dashboard.AdminDashboardDTO;
import lk.ijse.NexaSupply.dto.product.ProductResponseDTO;
import lk.ijse.NexaSupply.dto.report.OrderInvoiceReportDTO;
import lk.ijse.NexaSupply.dto.report.OrderItemReportDTO;
import lk.ijse.NexaSupply.dto.restock.RestockResponseDTO;
import lk.ijse.NexaSupply.dto.shipment.ShipmentResponseDTO;
import lk.ijse.NexaSupply.entity.ChatLog;
import lk.ijse.NexaSupply.enumeration.ChatIntent;
import lk.ijse.NexaSupply.enumeration.DriverStatus;
import lk.ijse.NexaSupply.enumeration.Role;
import lk.ijse.NexaSupply.repository.ChatLogRepository;
import lk.ijse.NexaSupply.service.AiToolsService;
import lk.ijse.NexaSupply.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatLogRepository chatLogRepository;
    private final AiToolsService aiToolsService;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Override
    public ChatResponseDTO processChat(ChatRequestDTO requestDTO, String userEmail, Role userRole) {
        log.info("Execute processChat method for user: {} | Role: {}", userEmail, userRole);

        String userPrompt = requestDTO.getMessage();

        String dbContext = buildRoleBasedContext(userEmail, userRole, userPrompt);

        String dynamicSystemPrompt = String.format(
                "You are NexaSupply AI Assistant. You MUST ONLY answer questions strictly related to NexaSupply system.\n" +
                        "User Context -> Role: %s | Email: %s\n\n" +
                        "--- ACCESSIBLE REAL-TIME SYSTEM CONTEXT ---\n%s\n-------------------\n\n" +
                        "Strict Instructions:\n" +
                        "1. Answer ONLY using the provided database context above.\n" +
                        "2. ALWAYS use 'LKR' or 'Rs.' as the currency symbol for prices, credit limits, balances, and totals. NEVER use '$' or USD.\n" + // <-- මේ Rule එක එකතු කරන්න
                        "3. If the user asks for information outside their role's scope or context, politely state that they do not have permission to view those details.\n" +
                        "4. Provide accurate, clear, and professional answers.",
                userRole, userEmail, dbContext
        );

        String fullUrl = apiUrl + "?key=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> systemInstruction = Map.of(
                "parts", List.of(Map.of("text", dynamicSystemPrompt))
        );

        Map<String, Object> userContent = Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", "User Role: " + userRole + "\nQuestion: " + userPrompt))
        );

        Map<String, Object> requestBody = Map.of(
                "system_instruction", systemInstruction,
                "contents", List.of(userContent)
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String aiReply = "";

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(fullUrl, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List candidates = (List) response.getBody().get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map firstCandidate = (Map) candidates.get(0);
                    Map content = (Map) firstCandidate.get("content");
                    List parts = (List) content.get("parts");
                    Map firstPart = (Map) parts.get(0);
                    aiReply = (String) firstPart.get("text");
                }
            }
        } catch (Exception e) {
            log.error("Error communicating with Gemini API", e);
            aiReply = "Sorry, I am having trouble connecting to AI services right now. Please try again later.";
        }

        try {
            ChatLog chatLog = new ChatLog();
            chatLog.setUserEmail(userEmail);
            chatLog.setUserRole(userRole);
            chatLog.setUserPrompt(userPrompt);
            chatLog.setAiResponse(aiReply);
            chatLog.setCreatedAt(LocalDateTime.now());
            chatLogRepository.save(chatLog);
        } catch (Exception e) {
            log.error("Failed to save ChatLog", e);
        }

        return new ChatResponseDTO(aiReply);
    }

    private String buildRoleBasedContext(String userEmail, Role userRole, String userPrompt) {
        StringBuilder context = new StringBuilder();

        if (ChatIntent.GLOBAL_SEARCH.matches(userPrompt)) {
            Map<String, Object> searchResults = aiToolsService.searchSystemData(userPrompt);
            if (searchResults != null && !searchResults.isEmpty()) {
                context.append("--- GLOBAL SEARCH RESULTS ---\n");
                context.append(searchResults).append("\n\n");
            }
        }

        if (ChatIntent.PRODUCT_LIST.matches(userPrompt)) {
            List<ProductResponseDTO> allProducts = aiToolsService.getAllProductsList();
            if (allProducts != null && !allProducts.isEmpty()) {
                context.append("--- ALL AVAILABLE PRODUCTS LIST ---\n");
                for (ProductResponseDTO p : allProducts) {
                    context.append(String.format("Code: %s | Name: %s | Price: LKR %.2f | Available Qty: %d | Category: %s\n",
                            p.getProductCode(), p.getProductName(), p.getUnitPrice(),
                            p.getAvailableQty(), p.getCategoryName()));
                }
                context.append("\n");
            }
        }

        if (ChatIntent.PRODUCT_INFO.matches(userPrompt)) {
            ProductResponseDTO productInfo = aiToolsService.getProductInformation(userPrompt);
            if (productInfo != null) {
                context.append("--- SPECIFIC PRODUCT INFO ---\n");
                context.append(String.format("Code: %s | Name: %s | Price: %.2f | Qty: %d | Category: %s\n\n",
                        productInfo.getProductCode(), productInfo.getProductName(), productInfo.getUnitPrice(),
                        productInfo.getAvailableQty(), productInfo.getCategoryName()));
            }
        }

        if (ChatIntent.CATEGORIES.matches(userPrompt)) {
            List<String> categories = aiToolsService.getCategoryList();
            if (categories != null && !categories.isEmpty()) {
                context.append("--- PRODUCT CATEGORIES ---\n");
                for (String category : categories) {
                    context.append("- ").append(category).append("\n");
                }
                context.append("\n");
            }
        }

        if (ChatIntent.ORDER_DETAILS.matches(userPrompt)) {
            OrderInvoiceReportDTO orderDetails = aiToolsService.getOrderDetailsAndStatus(userPrompt);
            if (orderDetails != null) {
                context.append("--- SPECIFIC ORDER DETAILS ---\n");
                context.append(String.format("Order Code: %s\nCustomer: %s\nShop: %s\nStatus: %s\nDate: %s\nTotal: LKR %.2f\nPaid: LKR %.2f\nBalance: LKR %.2f\n",
                        orderDetails.getOrderCode(),
                        orderDetails.getCustomerName() != null ? orderDetails.getCustomerName() : "N/A",
                        orderDetails.getShopName() != null ? orderDetails.getShopName() : "N/A",
                        orderDetails.getOrderStatus(),
                        orderDetails.getOrderDate(),
                        orderDetails.getGrandTotal(),
                        orderDetails.getPaidAmount(),
                        orderDetails.getBalanceAmount()));

                if (orderDetails.getItems() != null && !orderDetails.getItems().isEmpty()) {
                    context.append("Ordered Items:\n");
                    for (OrderItemReportDTO item : orderDetails.getItems()) {
                        context.append(String.format("- Product: %s | Qty: %d | Price: LKR %.2f | SubTotal: LKR %.2f\n",
                                item.getProductName(), item.getQuantity(), item.getUnitPrice(), item.getSubTotal()));
                    }
                }
                context.append("\n");
            }
        }

        if (ChatIntent.SHIPMENT_TRACKING.matches(userPrompt)) {
            ShipmentResponseDTO shipmentDetails = aiToolsService.getShipmentTrackingDetails(userPrompt);
            if (shipmentDetails != null) {
                context.append("--- SHIPMENT TRACKING DETAILS ---\n");
                context.append(String.format("Tracking No: %s | Status: %s | Driver: %s\n\n",
                        shipmentDetails.getTrackingNumber(), shipmentDetails.getStatus(), shipmentDetails.getDriverName()));
            }
        }

        if (ChatIntent.USER_PROFILE.matches(userPrompt)) {
            UserResponseDTO profile = aiToolsService.getUserProfileDetails(userEmail);
            if (profile != null) {
                context.append("--- CURRENT USER PROFILE ---\n");
                context.append(String.format("Code: %s | Name: %s | Shop: %s | Status: %s\n\n",
                        profile.getUserCode(), profile.getFullName(), profile.getShopName(), profile.getProfileStatus()));
            }
        }

        if (ChatIntent.NOTIFICATIONS.matches(userPrompt)) {
            List<String> notifications = aiToolsService.getNotificationHistory(userEmail);
            if (notifications != null && !notifications.isEmpty()) {
                context.append("--- USER NOTIFICATIONS ---\n");
                for (String notif : notifications) {
                    context.append(notif).append("\n");
                }
                context.append("\n");
            }
        }

        if (userRole == Role.ROLE_ADMIN) {

            if (ChatIntent.ADMIN_OVERVIEW.matches(userPrompt)) {
                AdminDashboardDTO overview = aiToolsService.getSystemOverviewData();
                if (overview != null) {
                    context.append("--- ADMIN SYSTEM OVERVIEW ---\n");
                    context.append(String.format("Total Revenue: %.2f | Pending Orders: %d | Low Stock Count: %d\n\n",
                            overview.getTotalRevenue(), overview.getPendingOrderCount(), overview.getLowStockProductsCount()));
                }
            }

            if (ChatIntent.DRIVER_STATUS.matches(userPrompt)) {
                List<DriverStatus> availableDrivers = aiToolsService.getDriverAvailabilityStatus(DriverStatus.AVAILABLE);
                context.append("--- DRIVER AVAILABILITY ---\n");
                context.append("Available Drivers Count: ").append(availableDrivers != null ? availableDrivers.size() : 0).append("\n\n");
            }

            if (ChatIntent.RESTOCK_SUPPLIER.matches(userPrompt)) {
                List<RestockResponseDTO> restocks = aiToolsService.getSupplierAndRestockHistory("");
                if (restocks != null && !restocks.isEmpty()) {
                    context.append("--- RESTOCK & SUPPLIER HISTORY ---\n");
                    for (RestockResponseDTO restock : restocks) {
                        context.append(String.format("RestockCode: %s | Supplier: %s | Cost: %.2f\n",
                                restock.getRestockCode(), restock.getSupplierName(), restock.getTotalCost()));
                    }
                    context.append("\n");
                }
            }

            if (ChatIntent.AUDIT_LOGS.matches(userPrompt)) {
                List<String> auditLogs = aiToolsService.getAuditLogSummary(userEmail);
                if (auditLogs != null && !auditLogs.isEmpty()) {
                    context.append("--- SYSTEM AUDIT LOGS ---\n");
                    for (String logMsg : auditLogs) {
                        context.append(logMsg).append("\n");
                    }
                    context.append("\n");
                }
            }

        } else if (userRole == Role.ROLE_RETAILER) {

            if (ChatIntent.CREDIT_LEDGER.matches(userPrompt)) {
                Map<String, Object> creditDetails = aiToolsService.getRetailerCreditAndLedgerDetails(userEmail);
                if (creditDetails != null && !creditDetails.isEmpty()) {
                    context.append("--- YOUR CREDIT & LEDGER HISTORY ---\n");
                    context.append("Credit Limit: ").append(creditDetails.get("creditLimit")).append("\n");
                    context.append("Ledger History: ").append(creditDetails.get("ledgerHistory")).append("\n\n");
                }
            }
        }

        if (context.length() == 0) {
            Map<String, Object> searchResults = aiToolsService.searchSystemData(userPrompt);
            if (searchResults != null && !searchResults.isEmpty()) {
                context.append("--- GLOBAL SEARCH RESULTS ---\n");
                context.append(searchResults).append("\n\n");
            }
        }

        return context.toString();
    }
}