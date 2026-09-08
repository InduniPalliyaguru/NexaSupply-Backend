package lk.ijse.NexaSupply.service.impl;

import jakarta.mail.internet.MimeMessage;
import lk.ijse.NexaSupply.exception.CustomException;
import lk.ijse.NexaSupply.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Async
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
            log.error("Failed to send email with attachment to {}:", e.getMessage());
            throw new CustomException(500, "Failed to send email with attachment to " + toEmail);
        }

    }

    @Async
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

    @Async
    @Override
    public void sendShipmentCreatedEmail(String toEmail, String customerName, String orderCode, String trackingNumber, String driverName, String driverPhone) {
        log.info("Sending shipment created email to {}", toEmail);

        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("🚚 Shipment Created for Order #" + orderCode);

            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e8d5ea; border-radius: 8px; padding: 25px; background-color: #ffffff;'>"
                    + "<h2 style='color: #3b0a45; text-align: center; margin-bottom: 20px;'>Shipment Assigned & Pending 🚚</h2>"
                    + "<p style='color: #333333; font-size: 15px;'>Dear <b>" + customerName + "</b>,</p>"
                    + "<p style='color: #555555; line-height: 1.5;'>A shipment has been created for your order <b>" + orderCode + "</b> and a delivery driver has been assigned.</p>"

                    + "<div style='background-color: #faf2fc; border-left: 4px solid #6f2c91; padding: 18px; margin: 20px 0; border-radius: 6px;'>"
                    + "<h4 style='margin: 0 0 10px 0; color: #4a005b; text-transform: uppercase; letter-spacing: 0.5px;'>Shipment & Driver Details</h4>"
                    + "<p style='margin: 6px 0; color: #333333;'><b>Order Code:</b> " + orderCode + "</p>"
                    + "<p style='margin: 6px 0; color: #333333;'><b>Tracking Number:</b> <span style='color: #6f2c91; font-weight: bold;'>" + trackingNumber + "</span></p>"
                    + "<p style='margin: 6px 0; color: #333333;'><b>Driver Name:</b> " + driverName + "</p>"
                    + "<p style='margin: 6px 0; color: #333333;'><b>Driver Contact:</b> " + driverPhone + "</p>"
                    + "<p style='margin: 6px 0; color: #333333;'><b>Status:</b> <span style='color: #d97706; font-weight: bold;'>PENDING DISPATCH</span></p>"
                    + "</div>"

                    + "<p style='color: #555555; line-height: 1.5;'>You will receive another update as soon as the driver dispatches your package.</p>"
                    + "<br/>"
                    + "<p style='color: #333333; margin: 0;'>Best Regards,<br/><b style='color: #4a005b;'>NexaSupply Distribution Team</b></p>"
                    + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Shipment creation email sent successfully to {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send shipment creation email to {}: ", toEmail, e);
            throw new CustomException(500, "Failed to send shipment email to " + toEmail);
        }

    }

    @Async
    @Override
    public void sendShipmentStatusUpdateEmail(String toEmail, String customerName, String orderCode, String trackingNumber, String driverName, String driverPhone, String status) {
        log.info("Sending shipment status update email to {}", toEmail);

        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("📦 Shipment Update: " + status + " - Order #" + orderCode);

            String statusBadgeColor = status.equalsIgnoreCase("DISPATCHED") ? "#28a745" : "#6f2c91";

            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e8d5ea; border-radius: 8px; padding: 25px; background-color: #ffffff;'>"
                    + "<h2 style='color: #3b0a45; text-align: center; margin-bottom: 20px;'>Shipment Status Update 🚚💨</h2>"
                    + "<p style='color: #333333; font-size: 15px;'>Dear <b>" + customerName + "</b>,</p>"
                    + "<p style='color: #555555; line-height: 1.5;'>Your order <b>" + orderCode + "</b> shipment status has been updated to <b>" + status + "</b>.</p>"

                    + "<div style='background-color: #faf2fc; border-left: 4px solid #6f2c91; padding: 18px; margin: 20px 0; border-radius: 6px;'>"
                    + "<h4 style='margin: 0 0 10px 0; color: #4a005b; text-transform: uppercase; letter-spacing: 0.5px;'>Delivery Details</h4>"
                    + "<p style='margin: 6px 0; color: #333333;'><b>Order Code:</b> " + orderCode + "</p>"
                    + "<p style='margin: 6px 0; color: #333333;'><b>Tracking Number:</b> <span style='color: #6f2c91; font-weight: bold;'>" + trackingNumber + "</span></p>"
                    + "<p style='margin: 6px 0; color: #333333;'><b>Driver Name:</b> " + driverName + "</p>"
                    + "<p style='margin: 6px 0; color: #333333;'><b>Driver Contact:</b> " + driverPhone + "</p>"
                    + "<p style='margin: 6px 0; color: #333333;'><b>Current Status:</b> <span style='color: " + statusBadgeColor + "; font-weight: bold;'>" + status + "</span></p>"
                    + "</div>"

                    + "<p style='color: #555555; line-height: 1.5;'>If you have any questions regarding delivery, feel free to contact the driver directly.</p>"
                    + "<br/>"
                    + "<p style='color: #333333; margin: 0;'>Best Regards,<br/><b style='color: #4a005b;'>NexaSupply Distribution Team</b></p>"
                    + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Shipment status update email sent successfully to {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send shipment status update email to {}: ", toEmail, e);
            throw new CustomException(500, "Failed to send shipment status update email to " + toEmail);
        }

    }

    @Async
    @Override
    public void sendOtpEmail(String toEmail, String otpCode) {
        log.info("Sending OTP email to {}", toEmail);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("🔒 Password Reset Verification Code - NexaSupply");

            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e8d5ea; border-radius: 8px; padding: 25px; background-color: #ffffff;'>"
                    + "<h2 style='color: #3b0a45; text-align: center; margin-bottom: 20px;'>Password Reset Request 🔑</h2>"
                    + "<p style='color: #333333; font-size: 15px;'>Hello,</p>"
                    + "<p style='color: #555555; line-height: 1.5;'>We received a request to reset your password for your NexaSupply account. Use the OTP code below to proceed:</p>"

                    + "<div style='background-color: #faf2fc; border-left: 4px solid #6f2c91; padding: 20px; margin: 20px 0; border-radius: 6px; text-align: center;'>"
                    + "<h4 style='margin: 0 0 10px 0; color: #4a005b; text-transform: uppercase; letter-spacing: 0.5px;'>Your One-Time Password (OTP)</h4>"
                    + "<div style='font-size: 32px; font-weight: bold; color: #6f2c91; letter-spacing: 6px; margin: 10px 0;'>" + otpCode + "</div>"
                    + "<p style='margin: 5px 0 0 0; color: #777777; font-size: 13px;'>This code is valid for <b>5 minutes</b>.</p>"
                    + "</div>"

                    + "<p style='color: #555555; line-height: 1.5;'>If you did not request a password reset, please ignore this email or contact support if you have concerns.</p>"
                    + "<br/>"
                    + "<p style='color: #333333; margin: 0;'>Best Regards,<br/><b style='color: #4a005b;'>NexaSupply Security Team</b></p>"
                    + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Password reset OTP email sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
            throw new CustomException(500, "Email sending failed. Please try again later.");
        }
    }

}
