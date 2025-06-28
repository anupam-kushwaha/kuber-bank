package com.anx.kuber_bank.service;

import com.anx.kuber_bank.dto.EmailDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService{
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${send.email.alerts}")
    private boolean sendEmailAlerts;

    @Override
    public void sendEmailAlerts(EmailDetails emailDetails) {
        try {
            if (!sendEmailAlerts) {
                log.info("Email alerts are disabled, not sending email to {}", emailDetails.getRecipient());
                return;
            }
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(senderEmail);
            simpleMailMessage.setTo(emailDetails.getRecipient());
            simpleMailMessage.setText(emailDetails.getMessageBody());
            simpleMailMessage.setSubject(emailDetails.getSubject());

            javaMailSender.send(simpleMailMessage);
            log.info("Mail sent successfully to {}", emailDetails.getRecipient());
        } catch (Exception e) {
            log.info("Error while sending mail : ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendEmailWithAttachment(EmailDetails emailDetails) {
        if (!sendEmailAlerts) {
            log.info("Email alerts are disabled, not sending email with attachment to {}", emailDetails.getRecipient());
            return;
        }

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(senderEmail);
            mimeMessageHelper.setTo(emailDetails.getRecipient());
            mimeMessageHelper.setSubject(emailDetails.getSubject());
            mimeMessageHelper.setText(emailDetails.getMessageBody());

            FileSystemResource file = new FileSystemResource(new File(emailDetails.getAttachment()));
            mimeMessageHelper.addAttachment(file.getFilename(), file);
            javaMailSender.send(mimeMessage);
            log.info("Attachment mail successfully sent to {}", emailDetails.getRecipient());
            log.info("Mail file path : {}", file.getPath());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
