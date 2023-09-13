package io.github.mateusz00.service.deck;

import org.springframework.stereotype.Service;

import io.github.mateusz00.dao.DeckReviewStatisticsRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class DeckReviewStatisticsService
{
    private final DeckReviewStatisticsRepository deckReviewStatisticsRepository;
}
