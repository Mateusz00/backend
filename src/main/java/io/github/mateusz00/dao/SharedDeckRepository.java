package io.github.mateusz00.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import io.github.mateusz00.entity.SharedDeck;

public interface SharedDeckRepository extends MongoRepository<SharedDeck, String>
{
}