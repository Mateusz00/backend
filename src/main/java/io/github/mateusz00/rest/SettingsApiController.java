package io.github.mateusz00.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import io.github.mateusz00.api.SettingsApi;
import io.github.mateusz00.api.model.Grammar;
import io.github.mateusz00.api.model.Language;
import io.github.mateusz00.api.model.UserGrammarSettingsSearchResult;
import io.github.mateusz00.api.model.UserGrammarSettingsSearchResults;
import io.github.mateusz00.api.model.UserGrammarSettingsUpsert;
import io.github.mateusz00.api.model.UserSettings;
import io.github.mateusz00.mapper.SettingsMapper;
import io.github.mateusz00.service.SettingsService;
import io.github.mateusz00.service.UserProvider;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SettingsApiController implements SettingsApi
{
    private final UserProvider userProvider;
    private final SettingsService settingsService;
    private final SettingsMapper settingsMapper;

    @Override
    public ResponseEntity<Void> deleteUserGrammarSettings(String grammarSettingId)
    {
        return null; // TODO when grammar
    }

    @Override
    public ResponseEntity<UserGrammarSettingsSearchResults> getUserGrammarSettings(Grammar grammar, Language language)
    {
        return null; // TODO when grammar
    }

    @Override
    public ResponseEntity<UserSettings> getUserSettings()
    {
        UserSettings settings = settingsMapper.map(settingsService.getUserSettings(userProvider.getUser()));
        return ResponseEntity.ok(settings);
    }

    @Override
    public ResponseEntity<UserSettings> updateUserSettings(UserSettings userSettings)
    {
        UserSettings settings = settingsMapper.map(settingsService.updateUserSettings(userProvider.getUser(), userSettings));
        return ResponseEntity.ok(settings);
    }

    @Override
    public ResponseEntity<UserGrammarSettingsSearchResult> upsertUserGrammarSettings(UserGrammarSettingsUpsert userGrammarSettingsUpsert)
    {
        return null; // TODO when grammar
    }
}
