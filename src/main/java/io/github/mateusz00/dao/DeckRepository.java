package io.github.mateusz00.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import io.github.mateusz00.entity.Deck;

public interface DeckRepository extends MongoRepository<Deck, String>
{
}