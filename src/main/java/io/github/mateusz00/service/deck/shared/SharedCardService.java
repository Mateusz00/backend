package io.github.mateusz00.service.deck.shared;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import io.github.mateusz00.dao.SharedCardRepository;
import io.github.mateusz00.entity.Card;
import io.github.mateusz00.entity.SharedCard;
import io.github.mateusz00.mapper.SharedCardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
class SharedCardService
{
    private final SharedCardRepository sharedCardRepository;
    private final SharedCardMapper sharedCardMapper;

    public void deleteAll(String deckId)
    {
        log.info("Deleting all cards for shared deck {}", deckId);
        sharedCardRepository.deleteAllBySharedDeckId(deckId);
    }

    public void createSharedCards(List<Card> cards, String deckId)
    {
        sharedCardRepository.insert(cards.stream()
                .map(card -> sharedCardMapper.map(card, deckId))
                .toList());
    }

    public Page<SharedCard> getCards(SharedCardPageQuery query)
    {
        return sharedCardRepository.findAllBySharedDeckId(query.getSharedDeckId(), query.getPageRequest());
    }

    public long getCardCount(String deckId)
    {
        return sharedCardRepository.countBySharedDeckId(deckId);
    }
}
