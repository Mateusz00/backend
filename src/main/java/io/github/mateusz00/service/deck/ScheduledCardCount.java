package io.github.mateusz00.service.deck;

public record ScheduledCardCount(int newCards, int cardsToReview)
{
    int getTotal()
    {
        return newCards + cardsToReview;
    }
}
