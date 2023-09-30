package io.github.mateusz00.service.deck;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import io.github.mateusz00.api.model.CardCreateRequest;
import io.github.mateusz00.api.model.CardUpdateRequest;
import io.github.mateusz00.api.model.DeckCreateRequest;
import io.github.mateusz00.api.model.DeckSearchResult;
import io.github.mateusz00.api.model.DeckUpdateRequest;
import io.github.mateusz00.api.model.DecksPage;
import io.github.mateusz00.api.model.SharedDeckCreateRequest;
import io.github.mateusz00.dao.DeckRepository;
import io.github.mateusz00.dto.UserInfo;
import io.github.mateusz00.entity.Card;
import io.github.mateusz00.entity.Deck;
import io.github.mateusz00.entity.SharedCard;
import io.github.mateusz00.entity.SharedDeck;
import io.github.mateusz00.exception.AuthorizationException;
import io.github.mateusz00.exception.BadRequestException;
import io.github.mateusz00.exception.InternalException;
import io.github.mateusz00.exception.NotFoundException;
import io.github.mateusz00.mapper.DeckMapper;
import io.github.mateusz00.service.deck.shared.SharedCardPageQuery;
import io.github.mateusz00.service.deck.shared.SharedDeckService;
import lombok.RequiredArgsConstructor;
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

    public Deck createDeck(DeckCreateRequest deckCreateRequest, String userId)
    {
        Deck deck = deckMapper.map(deckCreateRequest, userId);
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

        Deck savedDeck = deckRepository.insert(deck);
        statisticsService.createStatistics(deck.getId());
        return savedDeck;
    }

    private <T> void processBatchAsyncAndJoin(long itemCount, int batchSize, Function<Integer, T> supplier, Consumer<T> action)
    {
        int partitions = (int) ((itemCount - 1) / batchSize) + 1;
        List<CompletableFuture<Void>> features = new ArrayList<>(partitions);
        for (int i = 0; i < partitions; i++)
        {
            // TODO exception handling
            final int currentPartition = i; // Variable used in lambda expression should be final or effectively final
            CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> supplier.apply(currentPartition))
                    .thenAccept(action);
            features.add(future);
        }
        features.forEach(CompletableFuture::join);
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

    // TODO E2E tests in Java, 1 test.properties with link etc with prod commented out
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
        return sharedDeckService.createDeck(user, sharedDeckCreateRequest, savedCards);
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

    public Deck updateDeck(String deckId, DeckUpdateRequest updateRequest, UserInfo user)
    {        // TODO effective settings

        Deck deck = getDeck(deckId, user);
        deckMapper.update(deck, updateRequest);
        return deckRepository.save(deck);
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
        return cardService.updateCard(cardId, cardUpdateRequest);
    }

    public Card getCard(String deckId, UserInfo user, String cardId)
    {
        ensureDeckExistsAndUserHasAccess(deckId, user);
        return cardService.getCard(cardId);
    }

    public void removeCard(String deckId, UserInfo user, String cardId)
    {
        ensureDeckExistsAndUserHasAccess(deckId, user);
        cardService.removeCard(cardId);
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
