package io.github.mateusz00.service.deck;

import org.springframework.stereotype.Service;

import io.github.mateusz00.api.model.DeckCreateRequest;
import io.github.mateusz00.dao.DeckRepository;
import io.github.mateusz00.entity.Deck;
import io.github.mateusz00.mapper.DeckMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeckService
{
    private final DeckRepository deckRepository;
    private final CardService cardService;
    private final DeckReviewStatisticsService statisticsService;
    private final DeckMapper deckMapper;

    public Deck createNewDeck(DeckCreateRequest deckCreateRequest, String userId)
    {
        Deck deck = deckMapper.map(deckCreateRequest);
        deck.setUserId(userId);
        Deck savedDeck = deckRepository.insert(deck);
//        deckReviewStatisticsRepository.insert()
        return savedDeck;
    }
}
