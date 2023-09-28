package io.github.mateusz00.service.deck;

import org.springframework.stereotype.Service;

import io.github.mateusz00.api.model.CardCreateRequest;
import io.github.mateusz00.dao.CardRepository;
import io.github.mateusz00.entity.Card;
import io.github.mateusz00.mapper.CardMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class CardService
{
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    public void deleteAllCards(String deckId)
    {
        cardRepository.deleteAllByDeckId(deckId);
    }

    public Card createCard(CardCreateRequest cardCreateRequest, String deckId)
    {
        Card card = cardMapper.map(cardCreateRequest, deckId);
        return cardRepository.insert(card);
    }
}
