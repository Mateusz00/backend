package io.github.mateusz00.service.deck;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
@EqualsAndHashCode
public class ScheduledCardQuery
{
    @NonNull
    private final String deckId;
    private final LocalDateTime date;
    private final int newCards;
}
