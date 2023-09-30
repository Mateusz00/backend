package io.github.mateusz00.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import io.github.mateusz00.entity.UserSettings;

@Mapper
public interface SettingsMapper
{
    io.github.mateusz00.api.model.UserSettings map(UserSettings userSettings);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    void update(@MappingTarget UserSettings settings, io.github.mateusz00.api.model.UserSettings userSettings);
}
