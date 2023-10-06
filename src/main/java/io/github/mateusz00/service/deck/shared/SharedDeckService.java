package io.github.mateusz00.service.deck.shared;

import java.util.List;

import javax.annotation.Nullable;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import io.github.mateusz00.api.model.SharedDeckCreateRequest;
import io.github.mateusz00.api.model.SharedDeckUpdateRequest;
import io.github.mateusz00.dao.SharedDeckRepository;
import io.github.mateusz00.dto.UserInfo;
import io.github.mateusz00.entity.Card;
import io.github.mateusz00.entity.SharedCard;
import io.github.mateusz00.entity.SharedDeck;
import io.github.mateusz00.exception.AuthorizationException;
import io.github.mateusz00.exception.NotFoundException;
import io.github.mateusz00.mapper.SharedDeckMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static io.github.mateusz00.configuration.UserRole.ROLE_ADMIN;

@RequiredArgsConstructor
@Service
@Slf4j
public class SharedDeckService
{
    private final SharedDeckRepository sharedDeckRepository;
    private final SharedCardService sharedCardService;
    private final SharedDeckMapper sharedDeckMapper;

    public void deleteDeck(UserInfo user, String deckId)
    {
        SharedDeck deck = readAndEnsureExistsAndHasRights(user, deckId);
        log.info("Deleting shared deck {}", deckId);
        sharedDeckRepository.delete(deck);
        sharedCardService.deleteAll(deckId);
    }

    public SharedDeck getDeck(String deckId)
    {
        return sharedDeckRepository.findById(deckId).orElseThrow(() -> new NotFoundException("Shared deck " + deckId + " does not exist!"));
    }

    public boolean doesDeckExist(String deckId)
    {
        return sharedDeckRepository.findById(deckId).isPresent();
    }

    public SharedDeck updateDeck(UserInfo user, String deckId, SharedDeckUpdateRequest sharedDeckUpdateRequest)
    {
        SharedDeck deck = readAndEnsureExistsAndHasRights(user, deckId);
        sharedDeckMapper.update(deck, sharedDeckUpdateRequest);
        return sharedDeckRepository.save(deck);
    }

    public SharedDeck createDeck(UserInfo user, SharedDeckCreateRequest sharedDeckCreateRequest, long cardCount, @Nullable String sharedDeckId)
    {
        SharedDeck deck = sharedDeckMapper.map(sharedDeckCreateRequest, user.userId(), user.username(), cardCount);
        deck.setId(sharedDeckId);
        return sharedDeckRepository.insert(deck);
    }

    public long getCardCount(String deckId)
    {
        return sharedCardService.getCardCount(deckId);
    }

    public void addCards(List<Card> cards, String deckId)
    {
        sharedCardService.createSharedCards(cards, deckId);
    }

    public Page<SharedCard> getCards(SharedCardPageQuery query)
    {
        if (query.isCheckIfDeckExists())
        {
            getDeck(query.getSharedDeckId());
        }
        return sharedCardService.getCards(query);
    }

    private SharedDeck readAndEnsureExistsAndHasRights(UserInfo user, String deckId)
    {
        SharedDeck deck = sharedDeckRepository.findById(deckId).orElseThrow(NotFoundException::new);
        boolean isOwner = deck.getOwnerUserId().equals(user.userId());
        if (!isOwner && user.lacksRole(ROLE_ADMIN))
        {
            throw new AuthorizationException();
        }
        return deck;
    }

    public Page<SharedDeck> getPage(SharedDeckPageQuery query)
    {
        return sharedDeckRepository.listSharedDecks(query);
    }
}
