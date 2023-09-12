package io.github.mateusz00.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import io.github.mateusz00.entity.DeckReviewStatistics;

public interface DeckReviewStatisticsRepository extends MongoRepository<DeckReviewStatistics, String>
{
}