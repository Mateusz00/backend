package io.github.mateusz00.service;

import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.mateusz00.api.model.AccountRegistration;
import io.github.mateusz00.configuration.UserRole;
import io.github.mateusz00.dao.UserRepository;
import io.github.mateusz00.entity.User;
import io.github.mateusz00.exception.BadRequestException;
import lombok.RequiredArgsConstructor;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^A-Za-z\\d]");
    private static final Pattern LENGTH_PATTERN = Pattern.compile(".{8,}");
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(AccountRegistration accountRegistration) {
        if (userRepository.findByUsername(accountRegistration.getUsername()).isPresent())
            throw new BadRequestException("Username already used!");

        if (userRepository.findByEmail(accountRegistration.getEmail()).isPresent())
            throw new BadRequestException("Email already used!");

        validate(accountRegistration);
        var user = User.builder()
                .username(accountRegistration.getUsername())
                .password(passwordEncoder.encode(accountRegistration.getPassword()))
                .email(accountRegistration.getEmail())
                .roles(Set.of(UserRole.ROLE_USER))
                .build();
        return userRepository.save(user);
    }
    
    private void validate(AccountRegistration accountRegistration) {
        if (isBlank(accountRegistration.getUsername())) {
            throw new BadRequestException("Invalid username!");
        }
        if (isBlank(accountRegistration.getEmail())) {
            throw new BadRequestException("Invalid email!");
        }
        if (!isPasswordValid(accountRegistration.getPassword())) {
            throw new BadRequestException("Invalid password!");
        }
    }

    private boolean isPasswordValid(String password)
    {
        return isNotBlank(password)
                && LENGTH_PATTERN.matcher(password).find()
                && UPPERCASE_PATTERN.matcher(password).find()
                && LOWERCASE_PATTERN.matcher(password).find()
                && DIGIT_PATTERN.matcher(password).find()
                && SPECIAL_CHAR_PATTERN.matcher(password).find();
    }
}
