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
            helper.setSubject("Your NexaSupply Account Has Been Approved!");

            double limitValue = (creditLimit != null) ? creditLimit : 0.0;

            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e8d5ea; border-radius: 8px; padding: 25px; background-color: #ffffff;'>"
                    + "<h2 style='color: #3b0a45; text-align: center; margin-bottom: 20px;'>Welcome to NexaSupply! 🎉</h2>"
                    + "<p style='color: #333333; font-size: 15px;'>Dear <b>" + customerName + "</b>,</p>"
                    + "<p style='color: #555555; line-height: 1.5;'>Great news! Your account registration has been reviewed and <b>APPROVED</b> by our administration team.</p>"

                    + "<div style='background-color: #faf2fc; border-left: 4px solid #6f2c91; padding: 18px; margin: 20px 0; border-radius: 6px;'>"
                    + "<h4 style='margin: 0 0 10px 0; color: #4a005b; text-transform: uppercase; letter-spacing: 0.5px;'>Account Details</h4>"
                    + "<p style='margin: 6px 0; color: #333333;'><b>Registered Email:</b> " + toEmail + "</p>"
                    + "<p style='margin: 6px 0; color: #333333;'><b>Account Status:</b> <span style='color: #6f2c91; font-weight: bold;'>ACTIVE / APPROVED</span></p>"
                    + "<p style='margin: 6px 0; color: #333333;'><b>Approved Credit Limit:</b> <span style='font-size: 16px; color: #3b0a45; font-weight: bold;'>LKR " + String.format("%,.2f", limitValue) + "</span></p>"
                    + "</div>"

                    + "<p style='color: #555555; line-height: 1.5;'>You can now log in to your account using your credentials and start placing orders up to your allocated credit limit.</p>"
                    + "<br/>"
                    + "<p style='color: #333333; margin: 0;'>Best Regards,<br/><b style='color: #4a005b;'>NexaSupply Distribution Team</b></p>"
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
