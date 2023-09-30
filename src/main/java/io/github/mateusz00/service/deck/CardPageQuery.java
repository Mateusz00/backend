package io.github.mateusz00.service.deck;

import io.github.mateusz00.service.PageBaseQuery;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CardPageQuery extends PageBaseQuery
{
    @NonNull
    private final String deckId;
    private final CardStatusQuery statusQuery;
    private final boolean shouldGetTotal;
}
