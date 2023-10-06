package io.github.mateusz00.service.deck;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import io.github.mateusz00.api.model.CardCreateRequest;
import io.github.mateusz00.api.model.CardReviewAnswer;
import io.github.mateusz00.api.model.CardUpdateRequest;
import io.github.mateusz00.api.model.ScheduledCardReviews;
import io.github.mateusz00.dao.CardRepository;
import io.github.mateusz00.entity.Card;
import io.github.mateusz00.entity.CardStatistics;
import io.github.mateusz00.entity.CardStatus;
import io.github.mateusz00.entity.DeckSettings;
import io.github.mateusz00.entity.SharedCard;
import io.github.mateusz00.exception.InternalException;
import io.github.mateusz00.exception.NotFoundException;
import io.github.mateusz00.mapper.CardMapper;
import io.github.mateusz00.service.UtcDateTimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
class CardService
{
    public static final int REVIEWED_OLD_THRESHOLD = 30;
    public static final float NORMAL_RATE = 1.8f;
    public static final float MIN_EASE_RATE = 0.8f;
    public static final float FAIL_EASE_RATE_STEP = -0.04f;
    public static final float HARD_EASE_RATE_STEP = -0.02f;
    public static final float EASY_EASE_RATE_STEP = Math.abs(FAIL_EASE_RATE_STEP);
    public static final float MAX_EASE_RATE = 1.5f;
    public static final int NEW_CARD_REVIEW_FREQUENCY = 15;
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final UtcDateTimeProvider dateTimeProvider;

    void handleAnswer(String deckId, Card card, CardReviewAnswer answer, DeckSettings effectiveSettings)
    {
        updateStatisticsForAnswer(card, answer);
        updateFlags(card, answer, effectiveSettings);
        calculateNextReview(card, answer, effectiveSettings);
        updateCardStatus(card, effectiveSettings);
        cardRepository.save(card);
    }

    private void updateFlags(Card card, CardReviewAnswer answer, DeckSettings effectiveSettings)
    {
        if (answer == CardReviewAnswer.SUSPEND)
        {
            card.setSuspended(true);
        }
        if (card.getStatistics().getFails() >= effectiveSettings.getLeechThreshold())
        {
            card.setLeech(true);
        }
    }

    LocalDateTime predictNextReview(Card card, CardReviewAnswer answer, DeckSettings effectiveSettings)
    {
        Card cardCopy = cardMapper.deepCloneSlim(card);
        calculateNextReview(cardCopy, answer, effectiveSettings);
        return dateTimeProvider.convert(cardCopy.getNextReview());
    }

    private void calculateNextReview(Card card, CardReviewAnswer answer, DeckSettings effectiveSettings)
    {
        var now = dateTimeProvider.now();
        if (answer == CardReviewAnswer.SUSPEND)
        {
            card.setNextReview(dateTimeProvider.toInstant(now.plusDays(1)));
            card.setCurrentStep(0);
            return;
        }
        else if (answer == CardReviewAnswer.WRONG)
        {
            int newInterval = calculateNewInterval(effectiveSettings.getIntervalRateAfterFail(), card.getInterval(), effectiveSettings.getMaxInterval());
            card.setInterval(newInterval);
            card.setEaseRate(getNewEaseRate(card, FAIL_EASE_RATE_STEP));
            LocalDateTime newReviewDate = now.plusDays(newInterval);
            if (newInterval <= 0)
            {
                newReviewDate = newReviewDate.plusMinutes(20);
            }
            card.setNextReview(dateTimeProvider.toInstant(newReviewDate));
            card.setCurrentStep(0);
            return;
        }

        card.setCurrentStep(getNewStep(card, effectiveSettings, answer));
        card.setInterval(calculateNewInterval(card, effectiveSettings, answer));
        card.setNextReview(calculateNextReviewDate(now, card, effectiveSettings));
        card.setEaseRate(getNewEaseRate(card, getAnswerEaseRateStep(answer, card.getId())));
    }

    private Instant calculateNextReviewDate(LocalDateTime date, Card card, DeckSettings effectiveSettings)
    {
        if (card.getInterval() > 0)
        {
            return dateTimeProvider.toInstant(date.plusDays(card.getInterval()));
        }
        else
        {
            int minutes = effectiveSettings.getNewCardSteps()[card.getCurrentStep() - 1];
            LocalDateTime newDate = date.plusMinutes(minutes);
            if (dateTimeProvider.now().getDayOfMonth() != newDate.getDayOfMonth())
            {
                // Make sure that user will be able to complete all steps on the same day in one session
                newDate = newDate.truncatedTo(ChronoUnit.DAYS).minusSeconds(1);
            }
            return dateTimeProvider.toInstant(newDate);
        }
    }

    private float getNewEaseRate(Card card, float step)
    {
        float easeRate = card.getEaseRate();
        easeRate += step;
        easeRate = Math.max(MIN_EASE_RATE, easeRate);
        return Math.min(MAX_EASE_RATE, easeRate);
    }

    // TODO unit test
    private int getNewStep(Card card, DeckSettings effectiveSettings, CardReviewAnswer answer)
    {
        if (card.getInterval() > 0 || card.getStatus() != CardStatus.NEW || card.getStatus() == CardStatus.NEW_IN_REVIEW)
        {
            return 0;
        }
        int maxSteps = effectiveSettings.getNewCardSteps().length;
        if (answer == CardReviewAnswer.EASY)
        {
            return maxSteps + 1;
        }
        return Math.min(card.getCurrentStep() + 1, maxSteps + 1);
    }

    private int calculateNewInterval(Card card, DeckSettings effectiveSettings, CardReviewAnswer answer)
    {
        if ((card.getStatus() == CardStatus.NEW || card.getStatus() == CardStatus.NEW_IN_REVIEW) &&
                card.getCurrentStep() < effectiveSettings.getNewCardSteps().length + 1)
        {
            return 0;
        }
        float easeModifier = effectiveSettings.getGlobalEaseModifier() * card.getEaseRate();
        float answerModifier = getAnswerModifier(card.getId(), answer, effectiveSettings);
        int newInterval = calculateNewInterval(easeModifier * answerModifier, card.getInterval(), effectiveSettings.getMaxInterval());
        int intervalDiff = newInterval - card.getInterval();
        if (intervalDiff >= 0 && intervalDiff < effectiveSettings.getMinIntervalIncrease())
        {
            newInterval = card.getInterval() + effectiveSettings.getMinIntervalIncrease();
        }
        return newInterval;
    }

    private int calculateNewInterval(float rate, int currentInterval, int maxInterval)
    {
        int newInterval = Math.max((int) (rate * currentInterval), 1);
        return Math.min(newInterval, maxInterval);
    }

    private float getAnswerEaseRateStep(CardReviewAnswer answer, String cardId)
    {
        return switch (answer)
                {
                    case HARD -> HARD_EASE_RATE_STEP;
                    case GOOD -> 0.f;
                    case EASY -> EASY_EASE_RATE_STEP;
                    default ->
                    {
                        log.error("Unhandled answer {} for card {}", answer, cardId);
                        throw new InternalException("Unhandled answer " + answer);
                    }
                };
    }

    private float getAnswerModifier(String cardId, CardReviewAnswer answer, DeckSettings effectiveSettings)
    {
        return switch (answer)
                {
                    case HARD -> effectiveSettings.getHardRate();
                    case GOOD -> NORMAL_RATE;
                    case EASY -> effectiveSettings.getEasyRate();
                    default ->
                    {
                        log.error("Unhandled answer {} for card {}", answer, cardId);
                        throw new InternalException("Unhandled answer " + answer);
                    }
                };
    }

    private void updateCardStatus(Card card, DeckSettings effectiveSettings)
    {
        if (card.getInterval() > REVIEWED_OLD_THRESHOLD)
        {
            card.setStatus(CardStatus.REVIEWED_OLD);
        }
        else if (card.getCurrentStep() > effectiveSettings.getNewCardSteps().length + 1)
        {
            card.setStatus(CardStatus.REVIEWED_YOUNG);
        }
        else if (card.getStatus() == CardStatus.NEW)
        {
            card.setStatus(CardStatus.NEW_IN_REVIEW);
        }
    }

    private void updateStatisticsForAnswer(Card card, CardReviewAnswer answer)
    {
        CardStatistics statistics = card.getStatistics();
        if (answer == CardReviewAnswer.WRONG)
        {
            statistics.setFails(statistics.getFails() + 1);
        }
        statistics.setReviews(statistics.getReviews() + 1);
    }

    ScheduledCardCount getScheduledCardCount(ScheduledCardQuery query, Instant dateQuery)
    {
        long newCards = getNewCardsToReview(query);
        long scheduledCards = cardRepository.countByDeckIdAndNextReviewLessThanEqual(query.getDeckId(), dateQuery);
        return new ScheduledCardCount((int) newCards, (int) scheduledCards);
    }

    ScheduledCardReviews getCardToReview(ScheduledCardQuery query)
    {
        var date = dateTimeProvider.toInstant(query.getDate()
                .truncatedTo(ChronoUnit.DAYS)
                .plusDays(1)
                .minusSeconds(1)); // All cards scheduled for today
        ScheduledCardCount scheduledCardCount = getScheduledCardCount(query, date);
        Optional<Card> cardToReview = getCardToReview(query, scheduledCardCount.newCards(), scheduledCardCount.cardsToReview(), date);
        if (cardToReview.isEmpty())
        {
            return new ScheduledCardReviews()
                    .cardToReview(null)
                    .total(0);
        }
        return new ScheduledCardReviews()
                .cardToReview(cardMapper.map(cardToReview.get()))
                .total(scheduledCardCount.getTotal());
    }

    private long getNewCardsToReview(ScheduledCardQuery query)
    {
        if (query.getNewCards() > 0)
        {
            long newCardsInDeck = cardRepository.countByDeckIdAndStatus(query.getDeckId(), CardStatus.NEW.name());
            return Math.min(newCardsInDeck, query.getNewCards());
        }
        return 0;
    }

    private Optional<Card> getCardToReview(ScheduledCardQuery query, long newCards, long scheduledCards, Instant dateQuery)
    {
        if (newCards > scheduledCards / NEW_CARD_REVIEW_FREQUENCY)
        {
            var card = cardRepository.findByDeckIdAndStatus(query.getDeckId(), CardStatus.NEW.name());
            if (card.isEmpty())
            {
                return cardRepository.findByDeckIdAndNextReviewLessThanEqualOrderByNextReviewAsc(query.getDeckId(), dateQuery);
            }
            return card;
        }
        return cardRepository.findByDeckIdAndNextReviewLessThanEqualOrderByNextReviewAsc(query.getDeckId(), dateQuery);
    }

    public void deleteAllCards(String deckId)
    {
        cardRepository.deleteAllByDeckId(deckId);
    }

    public Card createCard(CardCreateRequest cardCreateRequest, String deckId)
    {
        Card card = cardMapper.map(cardCreateRequest, deckId);
        return cardRepository.insert(card);
    }

    public Card updateCard(String cardId, String deckId, CardUpdateRequest cardUpdateRequest)
    {
        Card card = getCard(cardId, deckId);
        cardMapper.patch(card, cardUpdateRequest);
        return cardRepository.save(card);
    }

    public Card getCard(String cardId, String deckId)
    {
        var notFoundException = new NotFoundException("Card", cardId);
        Card card = cardRepository.findById(cardId).orElseThrow(() -> notFoundException);
        if (!StringUtils.equals(card.getDeckId(), deckId))
        {
            throw notFoundException;
        }
        return card;
    }

    public void removeCard(String cardId, String deckId)
    {
        Card card = getCard(cardId, deckId);
        cardRepository.delete(card);
    }

    public Page<Card> getPage(CardPageQuery pageQuery)
    {
        return cardRepository.listCards(pageQuery);
    }

    public long getCount(String deckId)
    {
        return cardRepository.countByDeckId(deckId);
    }

    public List<Card> createCards(List<SharedCard> cards, String deckId)
    {
        return cardRepository.insert(cards.stream()
                .map(card -> cardMapper.map(card, deckId))
                .toList());
    }
}
