package io.github.mateusz00.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.mateusz00.configuration.UserRole;
import io.github.mateusz00.exception.InternalException;
import io.github.mateusz00.configuration.SecurityConstants;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TokenService
{
    static final String ROLES_CLAIM = "roles";
    private final SecurityConstants securityConstants;

    public String createToken(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return createToken(user.getUsername(), user.getAuthorities());
    }

    public String createToken(String username, Collection<? extends GrantedAuthority> authorities) {
        String jwt = JWT.create()
                .withClaim(ROLES_CLAIM, authorities.stream().map(GrantedAuthority::getAuthority).toList())
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + securityConstants.getExpirationTime()))
                .sign(Algorithm.HMAC512(securityConstants.getSecret()));
        return getTokenPrefix() + jwt;
    }

    public Optional<UsernamePasswordAuthenticationToken> getSpringAuthenticationToken(@Nullable String token) {
        if (StringUtils.isBlank(token) || !token.startsWith(securityConstants.getTokenPrefix())) {
            return Optional.empty();
        }

        DecodedJWT jwt = JWT.require(Algorithm.HMAC512(securityConstants.getSecret()))
                .build()
                .verify(token.replace(getTokenPrefix(), ""));
        String username = jwt.getSubject();
        List<UserRole> roles = jwt.getClaim(ROLES_CLAIM).asList(UserRole.class);

        if (StringUtils.isBlank(username)) {
            throw new InternalException("Invalid JWT with empty username " + username);
        }
        if (CollectionUtils.isEmpty(roles)) {
            throw new InternalException("Invalid JWT with empty roles " + roles);
        }

        return Optional.of(new UsernamePasswordAuthenticationToken(username, null, io.github.mateusz00.entity.User.getGrantedAuthorities(roles)));
    }

    private String getTokenPrefix() {
        return securityConstants.getTokenPrefix() + " ";
    }
}
