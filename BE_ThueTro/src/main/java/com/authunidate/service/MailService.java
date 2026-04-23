package com.authunidate.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public void sendEmail(String to, String loginUrl, String name) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("loginUrl", loginUrl);
        String html = templateEngine.process("email/welcome-email.html", context);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
        helper.setTo(to);
        helper.setSubject("[AuthUnidate] Welcome");
        helper.setText(html, true);
        javaMailSender.send(mimeMessage);
    }

    public void sendOTP(String to, String name, String otp) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("otp", otp);
        String html = templateEngine.process("email/OTP-email.html", context);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
        helper.setTo(to);
        helper.setSubject("[AuthUnidate] OTP Verification");
        helper.setText(html, true);
        javaMailSender.send(mimeMessage);
    }

    public void sendRegisterNotice(String to, String name) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("changedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        String html = templateEngine.process("email/ResetPassword-notice.html", context);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
        helper.setTo(to);
        helper.setSubject("[AuthUnidate] Password changed");
        helper.setText(html, true);
        javaMailSender.send(mimeMessage);
    }
}
