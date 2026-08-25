package lk.ijse.NexaSupply.enumeration;

import java.util.List;

public enum ChatIntent {
    GLOBAL_SEARCH(List.of("search", "find", "all", "get", "show")),
    PRODUCT_INFO(List.of("product", "item", "price", "unit price", "qty", "quantity", "stock", "available")),
    CATEGORIES(List.of("category", "categories", "type", "types", "group")),
    ORDER_DETAILS(List.of("order", "invoice", "buy", "purchase", "ord-")),
    SHIPMENT_TRACKING(List.of("track", "tracking", "shipment", "delivery", "dispatch", "driver name", "trk-")),
    USER_PROFILE(List.of("profile", "account", "my details", "shop", "user code")),
    NOTIFICATIONS(List.of("notification", "notifications", "alert", "alerts", "message", "messages")),

    ADMIN_OVERVIEW(List.of("revenue", "income", "sales", "profit", "earnings", "overview", "dashboard", "total", "summary", "pending")),
    DRIVER_STATUS(List.of("driver", "drivers", "availability", "available driver")),
    RESTOCK_SUPPLIER(List.of("supplier", "restock", "vendor", "purchase order", "restock history")),
    AUDIT_LOGS(List.of("audit", "log", "logs", "activity", "history", "security", "who did")),

    CREDIT_LEDGER(List.of("credit", "limit", "balance", "ledger", "money", "owe", "pay", "due"));

    private final List<String> keywords;

    ChatIntent(List<String> keywords) {
        this.keywords = keywords;
    }

    public boolean matches(String prompt) {
        if (prompt == null) return false;
        String lowerPrompt = prompt.toLowerCase();
        for (String keyword : this.keywords) {
            if (lowerPrompt.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}