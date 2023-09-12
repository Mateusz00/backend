package io.github.mateusz00.dto;

import java.util.Set;

import io.github.mateusz00.configuration.UserRole;
import lombok.Builder;

@Builder
public record UserInfo(String userId, String email, String username, Set<UserRole> roles)
{
}
