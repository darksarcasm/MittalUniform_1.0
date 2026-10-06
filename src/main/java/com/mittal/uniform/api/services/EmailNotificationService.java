package com.mittal.uniform.api.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService implements NotificationService {

    @Value("${mittal.uniforms.otp-expiry-minutes}")
    private int otpExpiryMinutes;

    private final JavaMailSender mailSender;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtp(String emailTarget, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sheetalagarwal528@gmail.com");
        message.setTo(emailTarget);
        message.setSubject("Mittal Uniforms - Password Reset Verification Code");
        message.setText("Your verification OTP is: " + otp + "\n\nThis code will expire in 15 minutes. Please do not share it with anyone.");

        mailSender.send(message);
    }
}