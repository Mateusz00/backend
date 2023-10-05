package io.github.mateusz00.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@PropertySource("classpath:jwt.properties")
@Getter
@RequiredArgsConstructor
public class SecurityConstants {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.prefix}")
    private String tokenPrefix;
    @Value("${jwt.expiration}")
    private Long expirationTime;
}
