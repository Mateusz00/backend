package io.github.mateusz00.service.deck;

import io.github.mateusz00.service.PageBaseQuery;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DeckPageQuery extends PageBaseQuery
{
    private final String language;
}
