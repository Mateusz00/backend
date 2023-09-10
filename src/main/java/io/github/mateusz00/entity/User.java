package io.github.mateusz00.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.github.mateusz00.configuration.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;
    private String password;
    private Set<UserRole> roles;
    @Indexed(unique = true)
    private String username;
    @Indexed(unique = true)
    private String email;

    public Collection<SimpleGrantedAuthority> getGrantedAuthorities() {
        return getGrantedAuthorities(new ArrayList<>(roles));
    }

    public static Collection<SimpleGrantedAuthority> getGrantedAuthorities(List<UserRole> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.name())).toList();
    }
}