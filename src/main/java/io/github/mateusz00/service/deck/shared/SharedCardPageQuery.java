package io.github.mateusz00.service.deck.shared;

import io.github.mateusz00.service.PageBaseQuery;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class SharedCardPageQuery extends PageBaseQuery
{
    private final String sharedDeckId;
    private final boolean checkIfDeckExists;
}
