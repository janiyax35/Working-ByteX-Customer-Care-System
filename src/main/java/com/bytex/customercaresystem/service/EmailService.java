package com.bytex.customercaresystem.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendEmailWithHtmlTemplate(String to, String subject, String templateName, Map<String, Object> templateModel) {
        Context context = new Context();
        context.setVariables(templateModel);

        String htmlBody = templateEngine.process(templateName, context);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            // Embed the logo
            ClassPathResource logo = new ClassPathResource("/static/images/logo.png");
            helper.addInline("logo.png", logo);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // Consider a more robust error handling
            throw new RuntimeException("Failed to send email", e);
        }
    }
}