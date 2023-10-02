package io.github.mateusz00.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import io.github.mateusz00.api.ScheduleApi;
import io.github.mateusz00.api.model.ScheduledCardReviews;
import io.github.mateusz00.api.model.ScheduledGrammarExercise;
import io.github.mateusz00.api.model.ScheduledGrammarExercises;
import io.github.mateusz00.service.UserProvider;
import io.github.mateusz00.service.deck.DeckService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ScheduleApiController implements ScheduleApi
{
    private final DeckService deckService;
    private final UserProvider userProvider;

    @Override
    public ResponseEntity<ScheduledCardReviews> getScheduledCards(String deckId)
    {
        return ResponseEntity.ok(deckService.getScheduledCards(deckId, userProvider.getUser()));
    }

    @Override
    public ResponseEntity<ScheduledGrammarExercise> getScheduledExercise(String exerciseId)
    {
        return null; // TODO when grammar
    }

    @Override
    public ResponseEntity<ScheduledGrammarExercises> getScheduledExercises()
    {
        return null; // TODO when grammar
    }
}
