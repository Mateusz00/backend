package io.github.mateusz00.filter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mateusz00.api.model.AccountLogin;
import io.github.mateusz00.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Override
    @SneakyThrows
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        AccountLogin credentials = OBJECT_MAPPER.readValue(req.getInputStream(), AccountLogin.class);
        var token = UsernamePasswordAuthenticationToken.unauthenticated(StringUtils.trim(credentials.getEmail()), StringUtils.trim(credentials.getPassword()));
        return authenticationManager.authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) {
        response.addHeader(HttpHeaders.AUTHORIZATION, tokenService.createToken(auth));
    }
}