package io.github.mateusz00.service.deck;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import io.github.mateusz00.api.model.CardCreateRequest;
import io.github.mateusz00.api.model.CardUpdateRequest;
import io.github.mateusz00.dao.CardRepository;
import io.github.mateusz00.entity.Card;
import io.github.mateusz00.entity.SharedCard;
import io.github.mateusz00.exception.NotFoundException;
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

    public Card updateCard(String cardId, CardUpdateRequest cardUpdateRequest)
    {
        Card card = getCard(cardId);
        cardMapper.patch(card, cardUpdateRequest);
        return cardRepository.save(card);
    }

    public Card getCard(String cardId)
    {
        return cardRepository.findById(cardId).orElseThrow(NotFoundException::new);
    }

    public void removeCard(String cardId)
    {
        Card card = getCard(cardId);
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
