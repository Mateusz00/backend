package io.github.mateusz00.service.deck.shared;

import io.github.mateusz00.service.PageBaseQuery;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SharedDeckPageQuery extends PageBaseQuery
{
    private final String language;
    private final String tag;
    private final String name;
}
