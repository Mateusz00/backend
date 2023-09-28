package io.github.mateusz00.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.github.mateusz00.api.model.Card;
import io.github.mateusz00.api.model.CardCreateRequest;

@Mapper
public interface CardMapper
{
    Card map(io.github.mateusz00.entity.Card card);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "nextReview", ignore = true)
    @Mapping(target = "easeRate", ignore = true)
    @Mapping(target = "interval", ignore = true)
    @Mapping(target = "suspended", ignore = true)
    @Mapping(target = "currentStep", ignore = true)
    @Mapping(target = "statistics", ignore = true)
    @Mapping(target = "leech", ignore = true)
    io.github.mateusz00.entity.Card map(CardCreateRequest cardCreateRequest, String deckId);
}
