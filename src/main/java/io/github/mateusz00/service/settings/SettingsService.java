package io.github.mateusz00.service.settings;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import io.github.mateusz00.dao.UserSettingsRepository;
import io.github.mateusz00.dto.UserInfo;
import io.github.mateusz00.entity.DeckSettings;
import io.github.mateusz00.entity.GrammarSettingsDetails;
import io.github.mateusz00.entity.SupportedLanguage;
import io.github.mateusz00.entity.UserSettings;
import io.github.mateusz00.entity.UserSiteSettings;
import io.github.mateusz00.exception.InternalException;
import io.github.mateusz00.mapper.SettingsMapper;
import io.github.mateusz00.service.UserCreatedEvent;
import io.github.mateusz00.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class SettingsService
{
    private final UserSettingsRepository userSettingsRepository;
    private final UserService userService;
    private final SettingsMapper settingsMapper;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    void initUserSettings(UserCreatedEvent event)
    {
        createSettings(new UserSettings()
                .setUserId(event.userId())
                .setSiteSettings(new UserSiteSettings()
                        .setDisplayLanguage(SupportedLanguage.ENGLISH)
                        .setInstructionsLanguage(null))
                .setDefaultDecksSettings(new DeckSettings()
                        .setNewCardsPerDay(10)
                        .setEasyRate(3.f)
                        .setHardRate(0.7f)
                        .setMaxInterval(365)
                        .setMinInterval(1)
                        .setGlobalEaseModifier(1.f)
                        .setIntervalRateAfterFail(0.1f)
                        .setLeechThreshold(12)
                        .setNewCardSteps(new int[]{7, 30}))
                .setDefaultGrammarSettings(new GrammarSettingsDetails()
                        .setMaxInterval(365)
                        .setIntervalRateAfterFail(0.1f)
                        .setFailThreshold(2)
                        .setExercisesPerDay(6)));
    }

    public UserSettings getUserSettings(UserInfo user)
    {
        UserSettings settings = userSettingsRepository.findByUserId(user.userId());
        if (settings == null)
        {
            userService.ensureUserExists(user.userId());
            log.error("Invalid user {}", user.userId());
            throw new InternalException("Invalid user " + user.userId());
        }
        return settings;
    }

    public UserSettings updateUserSettings(UserInfo user, io.github.mateusz00.api.model.UserSettings userSettings)
    {
        UserSettings settings = getUserSettings(user);
        settingsMapper.update(settings, userSettings);
        var savedSettings = userSettingsRepository.save(settings);
        if (userSettings.getDefaultDecksSettings() != null)
        {
            log.info("Publishing UserDeckSettingsUpdateEvent for user " + user.userId());
            eventPublisher.publishEvent(new UserDeckSettingsUpdateEvent(user.userId(), savedSettings.getDefaultDecksSettings()));
        }
        if (userSettings.getDefaultGrammarSettings() != null)
        {
            log.info("Publishing UserGrammarSettingsUpdateEvent for user " + user.userId());
            eventPublisher.publishEvent(new UserGrammarSettingsUpdateEvent(user.userId(), savedSettings.getDefaultGrammarSettings()));
        }
        return savedSettings;
    }

    public UserSettings createSettings(UserSettings settings)
    {
        return userSettingsRepository.insert(settings);
    }
}
