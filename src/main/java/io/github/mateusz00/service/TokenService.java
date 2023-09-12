package io.github.mateusz00.service;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.mateusz00.configuration.CustomUser;
import io.github.mateusz00.configuration.CustomUserDetails;
import io.github.mateusz00.configuration.SecurityConstants;
import io.github.mateusz00.configuration.UserRole;
import io.github.mateusz00.exception.InternalException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TokenService
{
    static final String ROLES_CLAIM = "roles";
    static final String USER_ID_CLAIM = "userId";
    static final String EMAIL_CLAIM = "email";
    private final SecurityConstants securityConstants;

    public String createToken(Authentication auth) {
        CustomUser user = (CustomUser) auth.getPrincipal();
        return createToken(user.getUsername(), user.getDetails());
    }

    public String createToken(String username, CustomUserDetails details) {
        String jwt = JWT.create()
                .withClaim(ROLES_CLAIM, details.roles().stream().map(Enum::toString).toList())
                .withClaim(USER_ID_CLAIM, details.userId())
                .withClaim(EMAIL_CLAIM, details.email())
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
        String email = jwt.getClaim(EMAIL_CLAIM).asString();
        String userId = jwt.getClaim(USER_ID_CLAIM).asString();
        ensureJwtIsValid(username, roles, email, userId);

        Collection<SimpleGrantedAuthority> grantedAuthorities = UserDetailsServiceImpl.getGrantedAuthorities(roles);
        var principal = new CustomUser(username, "JWT", grantedAuthorities, new CustomUserDetails(userId, email, new HashSet<>(roles)));
        var authToken = UsernamePasswordAuthenticationToken.authenticated(principal, null, grantedAuthorities);
        return Optional.of(authToken);
    }

    private void ensureJwtIsValid(String username, List<UserRole> roles, String email, String userId)
    {
        if (StringUtils.isBlank(username)) {
            throw new InternalException("Invalid JWT with empty username " + username);
        }
        if (StringUtils.isBlank(email)) {
            throw new InternalException("Invalid JWT with empty email " + email);
        }
        if (StringUtils.isBlank(userId)) {
            throw new InternalException("Invalid JWT with empty userId " + userId);
        }
        if (CollectionUtils.isEmpty(roles)) {
            throw new InternalException("Invalid JWT with empty roles " + roles);
        }
    }

    private String getTokenPrefix() {
        return securityConstants.getTokenPrefix() + " ";
    }
}
