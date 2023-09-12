package io.github.mateusz00.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.github.mateusz00.api.model.DeckCreateRequest;
import io.github.mateusz00.entity.Deck;

@Mapper
public interface DeckMapper
{
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "effectiveSettings", ignore = true)
    Deck map(DeckCreateRequest deckCreateRequest);

    io.github.mateusz00.api.model.Deck map(Deck newDeck);
}
