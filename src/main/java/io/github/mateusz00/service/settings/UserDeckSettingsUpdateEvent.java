package io.github.mateusz00.service.settings;

import io.github.mateusz00.entity.DeckSettings;

public record UserDeckSettingsUpdateEvent(String userId, DeckSettings defaultSettings)
{
}
