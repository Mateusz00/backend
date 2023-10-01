package io.github.mateusz00.service.settings;

import io.github.mateusz00.entity.GrammarSettingsDetails;

public record UserGrammarSettingsUpdateEvent(String userId, GrammarSettingsDetails defaultSettings)
{
}
