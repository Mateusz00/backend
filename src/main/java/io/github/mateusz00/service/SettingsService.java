package io.github.mateusz00.service;

import org.springframework.stereotype.Service;

import io.github.mateusz00.dao.UserSettingsRepository;
import io.github.mateusz00.dto.UserInfo;
import io.github.mateusz00.entity.UserSettings;
import io.github.mateusz00.mapper.SettingsMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SettingsService
{
    private final UserSettingsRepository userSettingsRepository;
    private final SettingsMapper settingsMapper;

    public UserSettings getUserSettings(UserInfo user)
    {
        return userSettingsRepository.findByUserId(user.userId());
    }

    public UserSettings updateUserSettings(UserInfo user, io.github.mateusz00.api.model.UserSettings userSettings)
    {
        UserSettings settings = getUserSettings(user);
        settingsMapper.update(settings, userSettings);
        return userSettingsRepository.save(settings);
    }
}
