package io.github.mateusz00.service;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.github.mateusz00.api.model.PasswordResetTokenRequest;
import io.github.mateusz00.dao.PasswordResetTokenRepository;
import io.github.mateusz00.entity.PasswordResetToken;
import io.github.mateusz00.exception.BadRequestException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PasswordResetTokenService
{
    private static final SecureRandom RANDOM = new SecureRandom();
    private final EmailService emailService;
    private final UserService userService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UtcDateTimeProvider utcDateTimeProvider;
    @Value("${password.reset.token.characterPool}")
    private String characterPool;
    @Value("${password.reset.token.codeLength}")
    private Integer codeLength;

    public void sendResetToken(PasswordResetTokenRequest request)
    {
        userService.findUser(request.getEmail())
                .orElseThrow(() -> new BadRequestException("No account associated with email " + request.getEmail()));
        String token = generateCode();
        passwordResetTokenRepository.save(new PasswordResetToken()
                .setToken(token)
                .setEmail(request.getEmail())
                .setExpiresAt(utcDateTimeProvider.toInstant(utcDateTimeProvider.now().plusHours(1))));
        emailService.sendPasswordResetEmail(request.getEmail(), token);
    }

    private String generateCode()
    {
        StringBuilder code = new StringBuilder(codeLength);
        for (int i = 0; i < codeLength; i++)
        {
            code.append(characterPool.charAt(RANDOM.nextInt(characterPool.length())));
        }
        return code.toString();
    }
}
