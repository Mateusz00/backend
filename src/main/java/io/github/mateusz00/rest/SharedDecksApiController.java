package io.github.mateusz00.rest;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import io.github.mateusz00.api.SharedDecksApi;
import io.github.mateusz00.api.model.SharedCardsPage;
import io.github.mateusz00.api.model.SharedDeck;
import io.github.mateusz00.api.model.SharedDeckCreateRequest;
import io.github.mateusz00.api.model.SharedDeckUpdateRequest;
import io.github.mateusz00.api.model.SharedDecksPage;
import io.github.mateusz00.entity.SharedCard;
import io.github.mateusz00.mapper.SharedCardMapper;
import io.github.mateusz00.mapper.SharedDeckMapper;
import io.github.mateusz00.service.UserProvider;
import io.github.mateusz00.service.deck.DeckService;
import io.github.mateusz00.service.deck.shared.SharedCardPageQuery;
import io.github.mateusz00.service.deck.shared.SharedDeckPageQuery;
import io.github.mateusz00.service.deck.shared.SharedDeckService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SharedDecksApiController implements SharedDecksApi
{
    private final UserProvider userProvider;
    private final SharedDeckService sharedDeckService;
    private final SharedDeckMapper sharedDeckMapper;
    private final SharedCardMapper sharedCardMapper;
    private final DeckService deckService;

    @Override
    public ResponseEntity<SharedDeck> createSharedDeck(SharedDeckCreateRequest sharedDeckCreateRequest)
    {
        SharedDeck deck = sharedDeckMapper.map(deckService.shareDeck(userProvider.getUser(), sharedDeckCreateRequest));
        return ResponseEntity.ok(deck);
    }

    @Override
    public ResponseEntity<Void> deleteSharedDeck(String deckId)
    {
        sharedDeckService.deleteDeck(userProvider.getUser(), deckId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<SharedDeck> getSharedDeck(String deckId)
    {
        SharedDeck deck = sharedDeckMapper.map(sharedDeckService.getDeck(deckId));
        return ResponseEntity.ok(deck);
    }

    @Override
    public ResponseEntity<SharedCardsPage> listSharedCards(String deckId, Integer limit, Integer offset)
    {
        var pageQuery = SharedCardPageQuery.builder()
                .sharedDeckId(deckId)
                .pageNumber(offset)
                .pageSize(limit)
                .checkIfDeckExists(true)
                .build();
        Page<SharedCard> page = sharedDeckService.getCards(pageQuery);
        return ResponseEntity.ok(new SharedCardsPage()
                .currentPage(pageQuery.getPageNumber())
                .limit(pageQuery.getPageSize())
                .pageTotal(page.getTotalPages())
                .total((int) page.getTotalElements())
                .cards(sharedCardMapper.mapCards(page.getContent())));
    }

    @Override
    public ResponseEntity<SharedDecksPage> listSharedDecks(Integer limit, Integer offset, String language, String sortBy, String tag, String name)
    {
        var query = SharedDeckPageQuery.builder()
                .sort(sharedDeckMapper.mapSort(sortBy))
                .pageNumber(offset)
                .pageSize(limit)
                .language(language)
                .tag(tag)
                .name(name)
                .build();
        Page<io.github.mateusz00.entity.SharedDeck> decksPage = sharedDeckService.getPage(query);
        return ResponseEntity.ok(new SharedDecksPage()
                .currentPage(query.getPageNumber())
                .limit(query.getPageSize())
                .pageTotal(decksPage.getTotalPages())
                .total((int) decksPage.getTotalElements())
                .decks(sharedDeckMapper.map(decksPage.getContent())));
    }

    @Override
    public ResponseEntity<SharedDeck> updateSharedDeck(String deckId, SharedDeckUpdateRequest sharedDeckUpdateRequest)
    {
        SharedDeck deck = sharedDeckMapper.map(sharedDeckService.updateDeck(userProvider.getUser(), deckId, sharedDeckUpdateRequest));
        return ResponseEntity.ok(deck);
    }
}
