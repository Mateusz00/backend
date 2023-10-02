package io.github.mateusz00.service.deck;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.mateusz00.api.model.CardReviewAnswer;
import io.github.mateusz00.dao.DeckReviewStatisticsRepository;
import io.github.mateusz00.entity.DeckReviewStatistics;
import io.github.mateusz00.entity.DeckReviewStatisticsEntry;
import io.github.mateusz00.entity.DeckReviewStatisticsSummary;
import io.github.mateusz00.service.UtcDateTimeProvider;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class DeckReviewStatisticsService
{
    private final DeckReviewStatisticsRepository deckReviewStatisticsRepository;
    private final UtcDateTimeProvider utcDateTimeProvider;

    public DeckReviewStatistics createStatistics(String deckId)
    {
        var date = utcDateTimeProvider.now();
        return deckReviewStatisticsRepository.insert(new DeckReviewStatistics()
                .setDeckId(deckId)
                .setYear(date.getYear())
                .setMonth(date.getMonthValue()));
    }

    public void deleteStatistics(String deckId)
    {
        deckReviewStatisticsRepository.deleteByDeckId(deckId);
    }

    private DeckReviewStatistics getOrCreateStatisticsForDate(String deckId, LocalDateTime date)
    {
        return deckReviewStatisticsRepository.findByDeckIdAndYearAndMonth(deckId, date.getYear(), date.getMonthValue())
                .orElseGet(() -> createStatistics(deckId));
    }

    /**
     * This method must not do any deck or card related authorization
     */
    void updateStatisticsForAnswer(String deckId, CardReviewAnswer answer) // TODO IT test
    {
        var date = utcDateTimeProvider.now();
        DeckReviewStatistics deckStatistics = getOrCreateStatisticsForDate(deckId, date);
        Optional<DeckReviewStatisticsEntry> oDaySummary = deckStatistics.getReviews()
                .stream()
                .filter(stats -> stats.getDay() == date.getDayOfMonth())
                .findAny();
        DeckReviewStatisticsEntry daySummary;
        if (oDaySummary.isEmpty())
        {
            daySummary = new DeckReviewStatisticsEntry().setDay(date.getDayOfMonth());
            deckStatistics.getReviews().add(daySummary);
        }
        else
        {
            daySummary = oDaySummary.get();
        }

        DeckReviewStatisticsSummary monthSummary = deckStatistics.getMonthSummary();
        switch (answer)
        {
            case WRONG ->
            {
                monthSummary.setWrong(monthSummary.getWrong() + 1);
                daySummary.setWrong(daySummary.getWrong() + 1);
            }
            case HARD ->
            {
                monthSummary.setHard(monthSummary.getHard() + 1);
                daySummary.setHard(daySummary.getHard() + 1);
            }
            case GOOD ->
            {
                monthSummary.setNormal(monthSummary.getNormal() + 1);
                daySummary.setNormal(daySummary.getNormal() + 1);
            }
            case EASY ->
            {
                monthSummary.setEasy(monthSummary.getEasy() + 1);
                daySummary.setEasy(daySummary.getEasy() + 1);
            }
            case SUSPEND ->
            {
                monthSummary.setSuspended(monthSummary.getSuspended() + 1);
                daySummary.setSuspended(daySummary.getSuspended() + 1);
            }
        }
        deckReviewStatisticsRepository.save(deckStatistics);
    }
}
