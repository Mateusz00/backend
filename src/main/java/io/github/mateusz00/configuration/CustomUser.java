package io.github.mateusz00.configuration;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public final class CustomUser extends User
{
    private final CustomUserDetails details;

    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities, CustomUserDetails details)
    {
        super(username, password, authorities);
        this.details = details;
    }
}
