package io.github.mateusz00.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import io.github.mateusz00.api.model.DeckCreateRequest;
import io.github.mateusz00.api.model.DeckSearchResult;
import io.github.mateusz00.api.model.DeckUpdateRequest;
import io.github.mateusz00.entity.Deck;

@Mapper
public interface DeckMapper
{
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "effectiveSettings", ignore = true)
    @Mapping(target = "customSettings", ignore = true)
    Deck map(DeckCreateRequest deckCreateRequest, String userId);

    io.github.mateusz00.api.model.Deck map(Deck newDeck);

    @Mapping(target = "waitingReviews", ignore = true) // Needs to be calculated
    DeckSearchResult mapToSearchResult(Deck deck);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sharedDeckId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "effectiveSettings", ignore = true)
    void update(@MappingTarget Deck deck, DeckUpdateRequest deckUpdateRequest); // TODO update effective too
}
