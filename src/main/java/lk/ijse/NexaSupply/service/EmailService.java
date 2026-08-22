package lk.ijse.NexaSupply.service;

public interface EmailService {

    void sendOrderInvoiceEmail(String toEmail, String subject, String body, byte[] pdfBytes, String fileName);

    void sendAccountApprovalEmail(String toEmail, String customerName, Double creditLimit);

    void sendShipmentCreatedEmail(String toEmail, String customerName, String orderCode, String trackingNumber, String driverName, String driverPhone);

    void sendShipmentStatusUpdateEmail(String toEmail, String customerName, String orderCode, String trackingNumber, String driverName, String driverPhone, String status);

    void sendOtpEmail(String toEmail, String otpCode);

}
