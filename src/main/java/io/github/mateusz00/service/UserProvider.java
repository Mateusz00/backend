package io.github.mateusz00.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import io.github.mateusz00.configuration.CustomUser;
import io.github.mateusz00.dto.UserInfo;

@Service
public class UserProvider
{
    public UserInfo getUser()
    {
        var user = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return UserInfo.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .roles(user.getRoles())
                .build();
    }
}
