package io.github.mateusz00.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import io.github.mateusz00.entity.DeckReviewStatistics;

public interface DeckReviewStatisticsRepository extends MongoRepository<DeckReviewStatistics, String>
{
    void deleteByDeckId(String deckId);
    Optional<DeckReviewStatistics> findByDeckIdAndYearAndMonth(String deckId, int year, int month);
}