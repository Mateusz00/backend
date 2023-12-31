package io.github.mateusz00.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import io.github.mateusz00.api.DecksApi;
import io.github.mateusz00.api.model.Deck;
import io.github.mateusz00.api.model.DeckCreateRequest;
import io.github.mateusz00.api.model.DeckStatistics;
import io.github.mateusz00.api.model.DeckUpdateRequest;
import io.github.mateusz00.api.model.DecksPage;
import io.github.mateusz00.api.model.StatisticsInterval;
import io.github.mateusz00.mapper.DeckMapper;
import io.github.mateusz00.service.UserProvider;
import io.github.mateusz00.service.deck.DeckPageQuery;
import io.github.mateusz00.service.deck.DeckService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DecksApiController implements DecksApi
{
    private final DeckService deckService;
    private final DeckMapper deckMapper;
    private final UserProvider userProvider;

    @Override
    public ResponseEntity<Deck> createDeck(DeckCreateRequest deckCreateRequest)
    {
        Deck deck = deckMapper.map(deckService.createDeck(deckCreateRequest, userProvider.getUser()));
        return ResponseEntity.ok(deck);
    }

    @Override
    public ResponseEntity<Void> deleteDeck(String deckId)
    {
        deckService.deleteDeck(deckId, userProvider.getUser());
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Deck> getDeck(String deckId)
    {
        Deck deck = deckMapper.map(deckService.getDeck(deckId, userProvider.getUser()));
        return ResponseEntity.ok(deck);
    }

    @Override
    public ResponseEntity<DeckStatistics> getDeckStatistics(String deckId, StatisticsInterval interval, String dateRangeStart, String dateRangeEnd)
    {
        return null; // TODO
    }

    @Override
    public ResponseEntity<DecksPage> listDecks(Integer limit, Integer offset, String language)
    {
        DecksPage decksPage = deckService.getPage(userProvider.getUser(), DeckPageQuery.builder()
                .pageNumber(offset)
                .pageSize(limit)
                .language(language)
                .build());
        return ResponseEntity.ok(decksPage);
    }

    @Override
    public ResponseEntity<Deck> updateDeck(String deckId, DeckUpdateRequest deckUpdateRequest)
    {
        Deck deck = deckMapper.map(deckService.updateDeck(deckId, deckUpdateRequest, userProvider.getUser()));
        return ResponseEntity.ok(deck);
    }
}
