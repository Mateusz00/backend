package io.github.mateusz00.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import io.github.mateusz00.exception.InternalException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmailService
{
    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(String email, String token)
    {
        String content = String.format("<big>Your verification code: <b>%s</b></big>", token);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);
        try
        {
            mimeMessageHelper.setFrom("noreply@demo-backend.com");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("Password Reset Code");
            mimeMessageHelper.setText(content, true);
        }
        catch (MessagingException e)
        {
            throw new InternalException("Could not create email!", e);
        }
        mailSender.send(message);
        log.info("Email with password reset token sent successfully to {}", email);
    }
}
