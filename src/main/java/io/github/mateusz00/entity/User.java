package io.github.mateusz00.entity;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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
@TypeAlias("User")
public class User {

    @Id
    private String id;
    private String password;
    private Set<UserRole> roles;
    @Indexed(unique = true)
    private String username;
    @Indexed(unique = true)
    private String email;
}