package io.github.mateusz00.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import io.github.mateusz00.filter.JWTAuthenticationFilter;
import io.github.mateusz00.filter.JWTAuthorizationFilter;
import io.github.mateusz00.service.TokenService;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig
{
    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;

    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        var authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http.cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(config -> config
                .requestMatchers("/login").anonymous()
                .requestMatchers("/accounts").anonymous()
                .requestMatchers("/accounts/password-resets").anonymous()
                .requestMatchers("/accounts/password-resets/token").anonymous()
                .requestMatchers("/settings").authenticated()
                .requestMatchers("/settings/grammar").authenticated()
                .requestMatchers("/resources").authenticated()
                .requestMatchers("/exercises/**").authenticated()
                .requestMatchers("/schedule/**").authenticated()
                .requestMatchers("/decks/**").authenticated()
                .requestMatchers("/shared/decks/**").authenticated()
                .anyRequest().denyAll()
        );

        http.addFilter(new JWTAuthenticationFilter(authenticationManager, tokenService))
                .addFilter(new JWTAuthorizationFilter(authenticationManager, tokenService));
        return http.build();
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}