package com.finalworks.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@finalworks.com}")
    private String fromEmail;

    public void sendRegistrationConfirmation(String toEmail, String name) {
        if (mailSender == null) {
            logger.warn("JavaMailSender not configured. Email not sent to: {}. " +
                    "Configure spring.mail properties to enable email sending.", toEmail);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to Final Works Platform - Registration Confirmed");
            message.setText(String.format(
                "Dear %s,\n\n" +
                "Thank you for registering with the Final Works Platform!\n\n" +
                "Your account has been successfully created. You can now:\n" +
                "- Upload your final works\n" +
                "- View and comment on other students' works\n" +
                "- Bookmark your favorite works\n\n" +
                "We're excited to have you on board!\n\n" +
                "Best regards,\n" +
                "The Final Works Team",
                name
            ));
            
            mailSender.send(message);
            logger.info("Registration confirmation email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send registration confirmation email to: {}", toEmail, e);
            // Don't throw exception - email failure shouldn't prevent registration
        }
    }
}
