package io.github.mateusz00.service.deck;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import io.github.mateusz00.api.model.CardCreateRequest;
import io.github.mateusz00.api.model.DeckCreateRequest;
import io.github.mateusz00.api.model.DeckSearchResult;
import io.github.mateusz00.api.model.DeckUpdateRequest;
import io.github.mateusz00.api.model.DecksPage;
import io.github.mateusz00.dao.DeckRepository;
import io.github.mateusz00.dto.UserInfo;
import io.github.mateusz00.entity.Card;
import io.github.mateusz00.entity.Deck;
import io.github.mateusz00.exception.AuthorizationException;
import io.github.mateusz00.exception.NotFoundException;
import io.github.mateusz00.mapper.DeckMapper;
import lombok.RequiredArgsConstructor;

import static io.github.mateusz00.configuration.UserRole.ROLE_ADMIN;

@Service
@RequiredArgsConstructor
public class DeckService
{
    private final DeckRepository deckRepository;
    private final CardService cardService;
    private final DeckReviewStatisticsService statisticsService;
    private final DeckMapper deckMapper;

    public Deck createDeck(DeckCreateRequest deckCreateRequest, String userId)
    {
        // TODO effective settings
        // TODO from shared (here or facade?)
        Deck deck = deckMapper.map(deckCreateRequest);
        deck.setUserId(userId);
        Deck savedDeck = deckRepository.insert(deck);
        statisticsService.createStatistics(deck.getId());
        return savedDeck;
    }

    public Deck updateDeck(String deckId, DeckUpdateRequest updateRequest, UserInfo user)
    {
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

    public Card addCard(String deckId, UserInfo user, CardCreateRequest cardCreateRequest)
    {
        getDeck(deckId, user);
        return cardService.createCard(cardCreateRequest, deckId);
    }

    public DecksPage getPage(UserInfo user, DeckQuery deckQuery)
    {
        Page<Deck> page = deckRepository.findAllByUserIdAndLanguage(user.userId(), deckQuery.getLanguage(), PageRequest.of(deckQuery.getPageNumber(), deckQuery.getPageSize()));
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
        if (!isOwner(user, deck) && user.lacksRole(ROLE_ADMIN))
        {
            throw new AuthorizationException();
        }
    }

    private boolean isOwner(UserInfo user, Deck deck)
    {
        return deck.getUserId().equals(user.userId());
    }
}
