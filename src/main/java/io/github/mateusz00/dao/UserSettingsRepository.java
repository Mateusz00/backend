package io.github.mateusz00.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import io.github.mateusz00.entity.UserSettings;

public interface UserSettingsRepository extends MongoRepository<UserSettings, String>
{
    UserSettings findByUserId(String userId);
}
