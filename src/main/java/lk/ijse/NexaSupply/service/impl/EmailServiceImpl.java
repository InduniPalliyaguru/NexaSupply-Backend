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
}
