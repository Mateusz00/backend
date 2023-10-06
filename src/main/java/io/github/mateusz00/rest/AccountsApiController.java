package io.github.mateusz00.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import io.github.mateusz00.api.AccountsApi;
import io.github.mateusz00.api.model.AccountRegistration;
import io.github.mateusz00.api.model.AccountUpdate;
import io.github.mateusz00.api.model.AccountsPage;
import io.github.mateusz00.api.model.PasswordResetRequest;
import io.github.mateusz00.api.model.PasswordResetTokenRequest;
import io.github.mateusz00.entity.User;
import io.github.mateusz00.service.PasswordResetTokenService;
import io.github.mateusz00.service.TokenService;
import io.github.mateusz00.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AccountsApiController implements AccountsApi
{
    private final UserService userService;
    private final HttpServletResponse response;
    private final TokenService tokenService;
    private final PasswordResetTokenService passwordResetTokenService;

    @Override
    public ResponseEntity<Void> createPasswordResetToken(PasswordResetTokenRequest passwordResetTokenRequest)
    {
        passwordResetTokenService.sendResetToken(passwordResetTokenRequest);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteAccount(String accountId)
    {
        return null; // TODO
    }

    @Override
    public ResponseEntity<AccountsPage> listAccounts(Integer limit, Integer offset, String username, String email)
    {
        return null; // TODO
    }

    @Override
    public ResponseEntity<Void> registerNewAccount(AccountRegistration accountRegistration)
    {
        User user = userService.registerUser(accountRegistration);
        response.addHeader(HttpHeaders.AUTHORIZATION, tokenService.createToken(user.getUsername(), user.getId(), user.getEmail(), user.getRoles()));
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> updateAccount(String accountId, AccountUpdate accountUpdate)
    {
        return null; // TODO
    }

    @Override
    public ResponseEntity<Void> verifyPasswordResetToken(PasswordResetRequest passwordResetRequest)
    {
        return null; // TODO
    }
}
