package io.github.mateusz00.entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Document
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias("PasswordResetToken")
@CompoundIndex(
        name = "passwordResetToken_email_token",
        def = "{'email': 1, 'token': 1}",
        unique = true)
public class PasswordResetToken
{
    @Id
    private String id;
    @NonNull
    private String email;
    @NonNull
    private String token;
    @NonNull
    @Indexed(expireAfter = "#{@passwordResetTokenExpireAfter}")
    private Instant expiresAt;
}
