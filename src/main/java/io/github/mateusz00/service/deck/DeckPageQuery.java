package io.github.mateusz00.service.deck;

import io.github.mateusz00.service.PageBaseQuery;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class DeckPageQuery extends PageBaseQuery
{
    private final String language;
}
