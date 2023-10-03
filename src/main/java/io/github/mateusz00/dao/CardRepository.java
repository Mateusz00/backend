package io.github.mateusz00.dao;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import io.github.mateusz00.entity.Card;

public interface CardRepository extends MongoRepository<Card, String>, CustomCardRepository
{
    void deleteAllByDeckId(String deckId);

    long countByDeckId(String deckId);

    Optional<Card> findByDeckIdAndStatus(String deckId, String status);

    long countByDeckIdAndStatus(String deckId, String status);

    long countByDeckIdAndNextReviewLessThanEqual(String deckId, Instant nextReview);

    Optional<Card> findByDeckIdAndNextReviewLessThanEqualOrderByNextReviewAsc(String deckId, Instant nextReview);
}