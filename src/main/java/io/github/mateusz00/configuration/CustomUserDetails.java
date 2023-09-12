package io.github.mateusz00.configuration;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public record CustomUserDetails(String userId, String email, Set<UserRole> roles) implements Serializable
{
    @Serial
    private static final long serialVersionUID = 951832625407626974L;
}
