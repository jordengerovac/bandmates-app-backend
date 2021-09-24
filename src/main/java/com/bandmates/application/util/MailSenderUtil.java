package com.bandmates.application.util;

import com.bandmates.application.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailSenderUtil {

    @Value("${bandmates.baseUrl}")
    private String bandmatesBaseUrl;

    @Autowired
    private JavaMailSender mailSender;

    public void sendConfirmationEmail(AppUser user) {
        String recipientAddress = user.getUsername();
        String subject = "Confirm your Bandmates account";
        String confirmationUrl = "/confirm-registration?token=" + user.getEmailRegistrationToken();
        String message = "To finish creating your Bandmates account, confirm your email address by clicking this link: ";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\r\n" + bandmatesBaseUrl + confirmationUrl);
        mailSender.send(email);
    }
}
