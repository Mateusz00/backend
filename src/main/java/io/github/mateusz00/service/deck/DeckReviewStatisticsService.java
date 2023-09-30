package io.github.mateusz00.service.deck;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import io.github.mateusz00.dao.DeckReviewStatisticsRepository;
import io.github.mateusz00.entity.DeckReviewStatistics;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class DeckReviewStatisticsService
{
    private final DeckReviewStatisticsRepository deckReviewStatisticsRepository;

    public DeckReviewStatistics createStatistics(String deckId)
    {
        var date = LocalDate.now();
        return deckReviewStatisticsRepository.insert(new DeckReviewStatistics()
                .setDeckId(deckId)
                .setYear(date.getYear())
                .setMonth(date.getMonthValue()));
    }

    public void deleteStatistics(String deckId)
    {
        deckReviewStatisticsRepository.deleteByDeckId(deckId);
    }
}
