package io.github.mateusz00.service;

import java.util.Collection;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.github.mateusz00.configuration.CustomUser;
import io.github.mateusz00.configuration.UserRole;
import io.github.mateusz00.dao.UserRepository;
import io.github.mateusz00.entity.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService
{
    private final UserRepository userRepository;

    public static Collection<SimpleGrantedAuthority> getGrantedAuthorities(Collection<UserRole> roles)
    {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.name())).toList();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(""));
        return new CustomUser(user.getUsername(), user.getPassword(), getGrantedAuthorities(user.getRoles()), user.getId(), user.getEmail(), user.getRoles());
    }
}