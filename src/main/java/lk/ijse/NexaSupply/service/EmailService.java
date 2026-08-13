package lk.ijse.NexaSupply.service;

public interface EmailService {

    void sendOrderInvoiceEmail(String toEmail, String subject, String body, byte[] pdfBytes, String fileName);

}
