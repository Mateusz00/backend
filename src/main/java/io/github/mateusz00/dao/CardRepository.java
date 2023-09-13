package io.github.mateusz00.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import io.github.mateusz00.entity.Card;

public interface CardRepository extends MongoRepository<Card, String>
{
}