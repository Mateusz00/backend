package io.github.mateusz00.rest;

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
        return null;
    }

    @Override
    public ResponseEntity<Card> getCard(String deckId, String cardId)
    {
        return null;
    }

    @Override
    public ResponseEntity<CardsPage> listCards(String deckId, Integer limit, Integer offset, String sortBy, String showOnly)
    {
        return null;
    }

    @Override
    public ResponseEntity<ScheduledCardReviews> submitAnswerForCard(String deckId, String cardId, SubmittedCardReviewAnswer submittedCardReviewAnswer)
    {
        return null;
    }

    @Override
    public ResponseEntity<Card> updateCard(String deckId, String cardId, CardUpdateRequest cardUpdateRequest)
    {
        return null;
    }
}
