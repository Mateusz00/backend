package io.github.mateusz00.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.mateusz00.entity.CustomDeckSettings;
import io.github.mateusz00.entity.DeckSettings;
import io.github.mateusz00.entity.UserSettings;

@Mapper
public interface SettingsMapper
{
    io.github.mateusz00.api.model.UserSettings map(UserSettings userSettings);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    void update(@MappingTarget UserSettings settings, io.github.mateusz00.api.model.UserSettings userSettings);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    DeckSettings mergeSettings(@MappingTarget DeckSettings defaultSettings, CustomDeckSettings customSettings);
}
