package io.github.mateusz00.service.deck;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import io.github.mateusz00.api.model.AnswerIntervalPrediction;
import io.github.mateusz00.api.model.AnswerIntervalPredictionUnit;
import io.github.mateusz00.api.model.CardCreateRequest;
import io.github.mateusz00.api.model.CardReviewAnswer;
import io.github.mateusz00.api.model.CardUpdateRequest;
import io.github.mateusz00.api.model.DeckCreateRequest;
import io.github.mateusz00.api.model.DeckSearchResult;
import io.github.mateusz00.api.model.DeckUpdateRequest;
import io.github.mateusz00.api.model.DecksPage;
import io.github.mateusz00.api.model.ScheduledCardReviews;
import io.github.mateusz00.api.model.SharedDeckCreateRequest;
import io.github.mateusz00.api.model.SubmittedCardReviewAnswer;
import io.github.mateusz00.dao.DeckRepository;
import io.github.mateusz00.dto.UserInfo;
import io.github.mateusz00.entity.Card;
import io.github.mateusz00.entity.CardCounter;
import io.github.mateusz00.entity.CardStatus;
import io.github.mateusz00.entity.Deck;
import io.github.mateusz00.entity.DeckSettings;
import io.github.mateusz00.entity.SharedCard;
import io.github.mateusz00.entity.SharedDeck;
import io.github.mateusz00.entity.UserSettings;
import io.github.mateusz00.exception.AuthorizationException;
import io.github.mateusz00.exception.BadRequestException;
import io.github.mateusz00.exception.InternalException;
import io.github.mateusz00.exception.NotFoundException;
import io.github.mateusz00.mapper.CardMapper;
import io.github.mateusz00.mapper.DeckMapper;
import io.github.mateusz00.mapper.SettingsMapper;
import io.github.mateusz00.service.UtcDateTimeProvider;
import io.github.mateusz00.service.deck.shared.SharedCardPageQuery;
import io.github.mateusz00.service.deck.shared.SharedDeckService;
import io.github.mateusz00.service.settings.SettingsService;
import io.github.mateusz00.service.settings.UserDeckSettingsUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static io.github.mateusz00.configuration.UserRole.ROLE_ADMIN;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeckService
{
    public static final int CARD_BATCH_SIZE = 100;
    private final DeckRepository deckRepository;
    private final CardService cardService;
    private final SharedDeckService sharedDeckService;
    private final DeckReviewStatisticsService statisticsService;
    private final DeckMapper deckMapper;
    private final CardMapper cardMapper;
    private final SettingsMapper settingsMapper;
    private final SettingsService settingsService;
    private final UtcDateTimeProvider dateTimeProvider;

    @EventListener
    void handleSettingsUpdateEvent(UserDeckSettingsUpdateEvent event)
    {
        List<Deck> decks = deckRepository.findAllByUserId(event.userId());
        decks.forEach(deck -> deck.setEffectiveSettings(settingsMapper.mergeSettings(event.defaultSettings(), deck.getCustomSettings())));
        deckRepository.saveAll(decks);
    }

    public Deck createDeck(DeckCreateRequest deckCreateRequest, UserInfo user)
    {
        Deck deck = deckMapper.map(deckCreateRequest, user.userId());
        var newDeckId = new ObjectId();
        deck.setId(newDeckId.toString());

        String sharedDeckId = deckCreateRequest.getSharedDeckId();
        if (StringUtils.isNotBlank(sharedDeckId))
        {
            SharedDeck sharedDeck = sharedDeckService.getDeck(sharedDeckId);
            long cardCount = sharedDeck.getCardCount();
            processBatchAsyncAndJoin(cardCount, CARD_BATCH_SIZE,
                    batchNumber -> sharedCardPageSupplier(sharedDeckId, batchNumber, cardCount),
                    page -> cardService.createCards(page.getContent(), newDeckId.toString()));
        }

        updateEffectiveSettings(user, deck);
        Deck savedDeck = deckRepository.insert(deck);
        statisticsService.createStatistics(deck.getId());
        return savedDeck;
    }

    @SneakyThrows
    private <R> void processBatchAsyncAndJoin(long itemCount, int batchSize, IntFunction<R> supplier, Consumer<R> action)
    {
        try
        {
            int partitions = (int) ((itemCount - 1) / batchSize) + 1;
            List<CompletableFuture<Void>> features = new ArrayList<>(partitions);
            for (int i = 0; i < partitions; i++)
            {
                final int currentPartition = i; // Variable used in lambda expression should be final or effectively final
                CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> supplier.apply(currentPartition))
                        .thenAccept(action);
                features.add(future);
            }
            features.forEach(CompletableFuture::join);
        }
        catch (CompletionException e)
        {
            throw e.getCause(); // Unwrap exception for better handling
        }
    }

    private Page<SharedCard> sharedCardPageSupplier(String sharedDeckId, int batchNumber, long cardCount)
    {
        var pageQuery = SharedCardPageQuery.builder()
                .sharedDeckId(sharedDeckId)
                .pageNumber(batchNumber)
                .pageSize(CARD_BATCH_SIZE)
                .checkIfDeckExists(false)
                .build();
        Page<SharedCard> cards = sharedDeckService.getCards(pageQuery);
        if (cards.getTotalElements() != cardCount)
        {
            if (sharedDeckService.doesDeckExist(sharedDeckId))
            {
                log.error("Card count for shared deck {} does not match actual count! [pageQuery=[{}], cardCount=[{}], actualCount=[{}]]",
                        sharedDeckId, pageQuery, cardCount, cards.getTotalElements());
                throw new InternalException("Shared deck card count mismatch!");
            }
            else
            {
                throw new NotFoundException("Shared deck " + sharedDeckId + " was deleted!");
            }
        }
        return cards;
    }

    public SharedDeck shareDeck(UserInfo user, SharedDeckCreateRequest sharedDeckCreateRequest)
    {
        String deckId = sharedDeckCreateRequest.getDeckId();
        ensureDeckExistsAndUserHasAccess(deckId, user);
        long cardCount = cardService.getCount(deckId);
        if (cardCount <= 0)
        {
            throw new BadRequestException("Deck " + deckId + " does not have any cards!");
        }

        var newDeckId = new ObjectId();
        processBatchAsyncAndJoin(cardCount, CARD_BATCH_SIZE,
                batchNumber -> cardPageSupplier(deckId, batchNumber),
                page -> sharedDeckService.addCards(page.getContent(), newDeckId.toString()));
        long savedCards = sharedDeckService.getCardCount(newDeckId.toString());
        return sharedDeckService.createDeck(user, sharedDeckCreateRequest, savedCards, newDeckId.toString());
    }

    private Page<Card> cardPageSupplier(String deckId, int batchNumber)
    {
        var pageQuery = CardPageQuery.builder()
                .deckId(deckId)
                .pageNumber(batchNumber)
                .pageSize(CARD_BATCH_SIZE)
                .shouldGetTotal(false)
                .build();
        return cardService.getPage(pageQuery);
    }

    public ScheduledCardReviews submitAnswerForCard(String deckId, String cardId, SubmittedCardReviewAnswer submittedCardReviewAnswer, UserInfo user)
    {
        Deck deck = getDeck(deckId, user);
        CardReviewAnswer answer = submittedCardReviewAnswer.getAnswer();
        if (answer == null)
        {
            throw new BadRequestException("Missing answer!");
        }

        DeckSettings effectiveSettings = deck.getEffectiveSettings();
        Card card = cardService.getCard(cardId, deckId);
        if (card.getStatus() == CardStatus.NEW && answer != CardReviewAnswer.SUSPEND)
        {
            restartNewCardsSeenCounterIfNecessary(deck);
            deck.getNewCardsSeen().incrementCount();
        }
        cardService.handleAnswer(deckId, card, answer, effectiveSettings);
        statisticsService.updateStatisticsForAnswer(deckId, answer);
        deckRepository.save(deck);
        return getScheduledCards(deck);
    }

    public ScheduledCardReviews getScheduledCards(String deckId, UserInfo user)
    {
        Deck deck = getDeck(deckId, user);
        return getScheduledCards(deck);
    }

    private ScheduledCardReviews getScheduledCards(Deck deck)
    {
        CardCounter cardCounter = deck.getNewCardsSeen();
        LocalDateTime currentDateTimeDays = dateTimeProvider.now().truncatedTo(ChronoUnit.DAYS);
        restartNewCardsSeenCounterIfNecessary(deck);
        int newCards = deck.getEffectiveSettings().getNewCardsPerDay() - cardCounter.getCount();
        ScheduledCardReviews cardReviews = cardService.getCardToReview(ScheduledCardQuery.builder()
                .deckId(deck.getId())
                .date(currentDateTimeDays)
                .newCards(newCards)
                .build());
        Card cardToReview = cardMapper.mapForReview(cardReviews.getCardToReview());
        if (cardToReview != null)
        {
            LocalDateTime nextReviewWrong = cardService.predictNextReview(cardToReview, CardReviewAnswer.WRONG, deck.getEffectiveSettings());
            LocalDateTime nextReviewHard = cardService.predictNextReview(cardToReview, CardReviewAnswer.HARD, deck.getEffectiveSettings());
            LocalDateTime nextReviewGood = cardService.predictNextReview(cardToReview, CardReviewAnswer.GOOD, deck.getEffectiveSettings());
            LocalDateTime nextReviewEasy = cardService.predictNextReview(cardToReview, CardReviewAnswer.EASY, deck.getEffectiveSettings());
            LocalDateTime now = dateTimeProvider.now();
            cardReviews.setWrongAnswerIntervalPrediction(getAnswerIntervalPrediction(nextReviewWrong, now));
            cardReviews.setHardAnswerIntervalPrediction(getAnswerIntervalPrediction(nextReviewHard, now));
            cardReviews.setGoodAnswerIntervalPrediction(getAnswerIntervalPrediction(nextReviewGood, now));
            cardReviews.setEasyAnswerIntervalPrediction(getAnswerIntervalPrediction(nextReviewEasy, now));
        }
        return cardReviews;
    }

    private AnswerIntervalPrediction getAnswerIntervalPrediction(LocalDateTime nextReview, LocalDateTime now)
    {
        Duration duration = Duration.between(now, nextReview);
        long diffInDays = duration.toDays();
        if (diffInDays > 0)
        {
            return new AnswerIntervalPrediction()
                    .unit(AnswerIntervalPredictionUnit.DAYS)
                    .value((int) diffInDays);
        }
        long diffInHours = duration.toHours();
        if (diffInHours > 0)
        {
            return new AnswerIntervalPrediction()
                    .unit(AnswerIntervalPredictionUnit.HOURS)
                    .value((int) diffInHours);
        }
        long diffInMinutes = duration.toMinutes();
        if (diffInMinutes > 0)
        {
            return new AnswerIntervalPrediction()
                    .unit(AnswerIntervalPredictionUnit.MINUTES)
                    .value((int) diffInMinutes);
        }
        throw new InternalException("Difference between dates smaller than 1 minute!");
    }

    private void restartNewCardsSeenCounterIfNecessary(Deck deck)
    {
        CardCounter cardCounter = deck.getNewCardsSeen();
        LocalDateTime currentDateTimeDays = dateTimeProvider.now().truncatedTo(ChronoUnit.DAYS);
        LocalDateTime lastNewCardsSeenDate = dateTimeProvider.convert(cardCounter.getDate()).truncatedTo(ChronoUnit.DAYS);
        if (!currentDateTimeDays.isEqual(lastNewCardsSeenDate))
        {
            cardCounter.setDate(Instant.now());
            cardCounter.setCount(0);
        }
    }

    public Deck updateDeck(String deckId, DeckUpdateRequest updateRequest, UserInfo user)
    {
        Deck deck = getDeck(deckId, user);
        deckMapper.update(deck, updateRequest);
        if (deck.getCustomSettings() != null)
        {
            updateEffectiveSettings(user, deck);
        }
        return deckRepository.save(deck);
    }

    private void updateEffectiveSettings(UserInfo user, Deck deck)
    {
        UserSettings settings = settingsService.getUserSettings(user);
        DeckSettings effectiveSettings = settingsMapper.mergeSettings(settings.getDefaultDecksSettings(), deck.getCustomSettings());
        deck.setEffectiveSettings(effectiveSettings);
    }

    public Deck getDeck(String deckId, UserInfo user)
    {
        Deck deck = deckRepository.findById(deckId).orElseThrow(NotFoundException::new);
        ensureHasAccess(user, deck);
        return deck;
    }

    public void deleteDeck(String deckId, UserInfo user)
    {
        Deck deck = deckRepository.findById(deckId).orElseThrow(NotFoundException::new);
        ensureHasAccess(user, deck);
        deckRepository.delete(deck);
        statisticsService.deleteStatistics(deckId);
        cardService.deleteAllCards(deckId);
    }

    public DecksPage getPage(UserInfo user, DeckPageQuery deckQuery)
    {
        Page<Deck> page = deckRepository.findAllByUserIdAndLanguage(user.userId(), deckQuery.getLanguage(), deckQuery.getPageRequest());
        return new DecksPage()
                .currentPage(deckQuery.getPageNumber())
                .limit(deckQuery.getPageSize())
                .pageTotal(page.getTotalPages())
                .total((int) page.getTotalElements())
                .decks(createDeckSearchResults(page.getContent()));
    }

    private List<DeckSearchResult> createDeckSearchResults(List<Deck> decks)
    {
        return decks.stream().map(this::createDeckSearchResult).toList();
    }

    private DeckSearchResult createDeckSearchResult(Deck deck)
    {
        return deckMapper.mapToSearchResult(deck)
                .waitingReviews(2); // TODO query cards
    }

    private void ensureHasAccess(UserInfo user, Deck deck)
    {
        boolean isOwner = deck.getUserId().equals(user.userId());
        if (!isOwner && user.lacksRole(ROLE_ADMIN))
        {
            throw new AuthorizationException();
        }
    }

    public Card addCard(String deckId, UserInfo user, CardCreateRequest cardCreateRequest)
    {
        ensureDeckExistsAndUserHasAccess(deckId, user);
        return cardService.createCard(cardCreateRequest, deckId);
    }

    public Card updateCard(String deckId, UserInfo user, String cardId, CardUpdateRequest cardUpdateRequest)
    {
        ensureDeckExistsAndUserHasAccess(deckId, user);
        return cardService.updateCard(cardId, deckId, cardUpdateRequest);
    }

    public Card getCard(String deckId, UserInfo user, String cardId)
    {
        ensureDeckExistsAndUserHasAccess(deckId, user);
        return cardService.getCard(cardId, deckId);
    }

    public void removeCard(String deckId, UserInfo user, String cardId)
    {
        ensureDeckExistsAndUserHasAccess(deckId, user);
        cardService.removeCard(cardId, deckId);
    }

    private void ensureDeckExistsAndUserHasAccess(String deckId, UserInfo user)
    {
        getDeck(deckId, user);
    }

    public Page<Card> getCardsPage(UserInfo user, CardPageQuery pageQuery)
    {
        ensureDeckExistsAndUserHasAccess(pageQuery.getDeckId(), user);
        return cardService.getPage(pageQuery);
    }
}
