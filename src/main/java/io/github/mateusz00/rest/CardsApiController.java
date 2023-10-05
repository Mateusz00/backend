package io.github.mateusz00.rest;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import io.github.mateusz00.api.CardsApi;
import io.github.mateusz00.api.model.Card;
import io.github.mateusz00.api.model.CardCreateRequest;
import io.github.mateusz00.api.model.CardUpdateRequest;
import io.github.mateusz00.api.model.CardsPage;
import io.github.mateusz00.api.model.ScheduledCardReviews;
import io.github.mateusz00.api.model.SubmittedCardReviewAnswer;
import io.github.mateusz00.mapper.CardMapper;
import io.github.mateusz00.service.UserProvider;
import io.github.mateusz00.service.deck.CardPageQuery;
import io.github.mateusz00.service.deck.DeckService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CardsApiController implements CardsApi
{
    private final DeckService deckService;
    private final CardMapper cardMapper;
    private final UserProvider userProvider;

    @Override
    public ResponseEntity<Card> createCard(String deckId, CardCreateRequest cardCreateRequest)
    {
        Card card = cardMapper.map(deckService.addCard(deckId, userProvider.getUser(), cardCreateRequest));
        return ResponseEntity.ok(card);
    }

    @Override
    public ResponseEntity<Void> deleteCard(String deckId, String cardId)
    {
        deckService.removeCard(deckId, userProvider.getUser(), cardId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Card> getCard(String deckId, String cardId)
    {
        Card card = cardMapper.map(deckService.getCard(deckId, userProvider.getUser(), cardId));
        return ResponseEntity.ok(card);
    }

    @Override
    public ResponseEntity<CardsPage> listCards(String deckId, Integer limit, Integer offset, String sortBy, String showOnly)
    {
        var pageQuery = CardPageQuery.builder()
                .pageNumber(offset)
                .pageSize(limit)
                .deckId(deckId)
                .statusQuery(cardMapper.mapCardStatusQuery(showOnly))
                .sort(cardMapper.mapCardSort(sortBy))
                .shouldGetTotal(true)
                .build();
        Page<io.github.mateusz00.entity.Card> page = deckService.getCardsPage(userProvider.getUser(), pageQuery);
        return ResponseEntity.ok(new CardsPage()
                .currentPage(pageQuery.getPageNumber())
                .limit(pageQuery.getPageSize())
                .pageTotal(page.getTotalPages())
                .total((int) page.getTotalElements())
                .cards(cardMapper.mapCards(page.getContent())));
    }

    @Override
    public ResponseEntity<ScheduledCardReviews> submitAnswerForCard(String deckId, String cardId, SubmittedCardReviewAnswer submittedCardReviewAnswer)
    {
        return ResponseEntity.ok(deckService.submitAnswerForCard(deckId, cardId, submittedCardReviewAnswer, userProvider.getUser()));
    }

    @Override
    public ResponseEntity<Card> patchCard(String deckId, String cardId, CardUpdateRequest cardUpdateRequest) // TODO Test
    {
        Card card = cardMapper.map(deckService.updateCard(deckId, userProvider.getUser(), cardId, cardUpdateRequest));
        return ResponseEntity.ok(card);
    }
}
