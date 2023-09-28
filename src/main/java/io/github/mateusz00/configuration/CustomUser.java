package io.github.mateusz00.configuration;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public final class CustomUser extends User
{
    private final String userId;
    private final String email;
    private final Set<UserRole> roles;

    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String userId, String email, Set<UserRole> roles)
    {
        super(username, password, authorities);
        this.userId = userId;
        this.email = email;
        this.roles = roles;
    }
}
