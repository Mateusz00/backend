package io.github.mateusz00.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import io.github.mateusz00.api.ExercisesApi;
import io.github.mateusz00.api.model.Grammar;
import io.github.mateusz00.api.model.GrammarExercise;
import io.github.mateusz00.api.model.GrammarExerciseUpsert;
import io.github.mateusz00.api.model.GrammarExercisesPage;
import io.github.mateusz00.api.model.SubmittedExerciseReviewAnswer;
import io.github.mateusz00.api.model.UseCase;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ExercisesApiController implements ExercisesApi
{

    @Override
    public ResponseEntity<GrammarExercise> createNewExercise(GrammarExerciseUpsert grammarExerciseUpsert)
    {
        return null; // TODO when grammar
    }

    @Override
    public ResponseEntity<Void> deleteExercise(String exerciseId)
    {
        return null; // TODO when grammar
    }

    @Override
    public ResponseEntity<GrammarExercise> getExercise(String exerciseId)
    {
        return null; // TODO when grammar
    }

    @Override
    public ResponseEntity<GrammarExercisesPage> listExercises(Integer limit, Integer offset, UseCase useCase, Grammar grammar)
    {
        return null; // TODO when grammar
    }

    @Override
    public ResponseEntity<Void> submitAnswerForExercise(SubmittedExerciseReviewAnswer submittedExerciseReviewAnswer)
    {
        return null; // TODO when grammar
    }

    @Override
    public ResponseEntity<GrammarExercise> updateExercise(String exerciseId, GrammarExerciseUpsert grammarExerciseUpsert)
    {
        return null; // TODO when grammar
    }
}
