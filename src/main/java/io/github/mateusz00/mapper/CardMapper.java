package io.github.mateusz00.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.EnumMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ValueMapping;
import org.mapstruct.control.DeepClone;

import io.github.mateusz00.api.model.Card;
import io.github.mateusz00.api.model.CardCreateRequest;
import io.github.mateusz00.api.model.CardUpdateRequest;
import io.github.mateusz00.entity.SharedCard;
import io.github.mateusz00.exception.BadRequestException;
import io.github.mateusz00.service.deck.CardSort;
import io.github.mateusz00.service.deck.CardStatusQuery;

@Mapper
public interface CardMapper
{
    Card map(io.github.mateusz00.entity.Card card);

    @BeanMapping(mappingControl = DeepClone.class)
    @Mapping(target = "front", ignore = true)
    @Mapping(target = "back", ignore = true)
    io.github.mateusz00.entity.Card deepCloneSlim(io.github.mateusz00.entity.Card card);

    List<Card> mapCards(List<io.github.mateusz00.entity.Card> card);

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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deckId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "nextReview", ignore = true)
    @Mapping(target = "easeRate", ignore = true)
    @Mapping(target = "interval", ignore = true)
    @Mapping(target = "currentStep", ignore = true)
    @Mapping(target = "statistics", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patch(@MappingTarget io.github.mateusz00.entity.Card card, CardUpdateRequest cardUpdateRequest);

    @EnumMapping(unexpectedValueMappingException = BadRequestException.class)
    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.THROW_EXCEPTION)
    CardSort mapCardSort(String sort);

    @EnumMapping(unexpectedValueMappingException = BadRequestException.class)
    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.THROW_EXCEPTION)
    CardStatusQuery mapCardStatusQuery(String query);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "nextReview", ignore = true)
    @Mapping(target = "easeRate", ignore = true)
    @Mapping(target = "interval", ignore = true)
    @Mapping(target = "suspended", ignore = true)
    @Mapping(target = "currentStep", ignore = true)
    @Mapping(target = "statistics", ignore = true)
    @Mapping(target = "leech", ignore = true)
    io.github.mateusz00.entity.Card map(SharedCard card, String deckId);

    @Mapping(target = "deckId", ignore = true)
    io.github.mateusz00.entity.Card mapForReview(Card card);
}
