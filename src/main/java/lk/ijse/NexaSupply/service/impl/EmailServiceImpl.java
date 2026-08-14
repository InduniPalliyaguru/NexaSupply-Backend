package lk.ijse.NexaSupply.service.impl;

import jakarta.mail.internet.MimeMessage;
import lk.ijse.NexaSupply.exception.CustomException;
import lk.ijse.NexaSupply.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOrderInvoiceEmail(String toEmail, String subject, String body, byte[] pdfBytes, String fileName) {
        log.info("Execute sendOrderInvoiceEmail");

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);

            ByteArrayResource pdfAttachment = new ByteArrayResource(pdfBytes);
            helper.addAttachment(fileName, pdfAttachment);

            mailSender.send(message);

        } catch (Exception e) {
            throw new CustomException(500, "Failed to send email with attachment to " + toEmail);
        }

    }

    @Override
    public void sendAccountApprovalEmail(String toEmail, String customerName, Double creditLimit) {
        log.info("Execute sendAccountApprovalEmail");

        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

            helper.setTo(toEmail);
            helper.setSubject("🎉 Your NexaSupply Account Has Been Approved!");

            double limitValue = (creditLimit != null) ? creditLimit : 0.0;

            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0; border-radius: 8px; padding: 20px;'>"
                    + "<h2 style='color: #2c3e50; text-align: center;'>Welcome to NexaSupply!</h2>"
                    + "<p>Dear <b>" + customerName + "</b>,</p>"
                    + "<p>Great news! Your account registration has been reviewed and <b>APPROVED</b> by our administration team.</p>"

                    + "<div style='background-color: #f8f9fa; border-left: 4px solid #28a745; padding: 15px; margin: 20px 0; border-radius: 4px;'>"
                    + "<h4 style='margin-top: 0; color: #28a745;'>Account Details:</h4>"
                    + "<p style='margin: 5px 0;'><b>Registered Email:</b> " + toEmail + "</p>"
                    + "<p style='margin: 5px 0;'><b>Account Status:</b> <span style='color: green; font-weight: bold;'>ACTIVE / APPROVED</span></p>"
                    + "<p style='margin: 5px 0;'><b>Approved Credit Limit:</b> <span style='font-size: 16px; color: #0d6efd; font-weight: bold;'>LKR " + String.format("%,.2f", limitValue) + "</span></p>"
                    + "</div>"

                    + "<p>You can now log in to your account using your credentials and start placing orders up to your allocated credit limit.</p>"
                    + "<br/>"
                    + "<p>Best Regards,<br/><b>NexaSupply Distribution Team</b></p>"
                    + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Account Approval Email sent successfully");

        } catch (Exception e) {
            log.error("Failed to send email with attachment to {}", toEmail);
            throw new CustomException(500, "Failed to send account approval email to " + toEmail);
        }

    }
}
