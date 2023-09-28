package io.github.mateusz00.dto;

import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import io.github.mateusz00.configuration.UserRole;
import lombok.Builder;

@Builder
public record UserInfo(String userId, String email, String username, Set<UserRole> roles)
{
    public boolean hasRole(UserRole role)
    {
        return CollectionUtils.containsAny(roles, role);
    }

    public boolean lacksRole(UserRole role)
    {
        return !hasRole(role);
    }
}
