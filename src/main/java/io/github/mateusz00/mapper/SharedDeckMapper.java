package io.github.mateusz00.mapper;

import java.util.List;

import org.mapstruct.EnumMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ValueMapping;

import io.github.mateusz00.api.model.SharedDeck;
import io.github.mateusz00.api.model.SharedDeckCreateRequest;
import io.github.mateusz00.api.model.SharedDeckSearchResult;
import io.github.mateusz00.api.model.SharedDeckUpdateRequest;
import io.github.mateusz00.exception.BadRequestException;
import io.github.mateusz00.service.deck.shared.SharedDeckSort;

@Mapper
public interface SharedDeckMapper
{
    SharedDeck map(io.github.mateusz00.entity.SharedDeck deck);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sharedAt", ignore = true)
    @Mapping(target = "popularity", ignore = true)
    io.github.mateusz00.entity.SharedDeck map(SharedDeckCreateRequest deck, String ownerUserId, String ownerUsername, long cardCount);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerUserId", ignore = true)
    @Mapping(target = "ownerUsername", ignore = true)
    @Mapping(target = "sharedAt", ignore = true)
    @Mapping(target = "cardCount", ignore = true)
    @Mapping(target = "popularity", ignore = true)
    void update(@MappingTarget io.github.mateusz00.entity.SharedDeck deck, SharedDeckUpdateRequest sharedDeckUpdateRequest);

    @EnumMapping(unexpectedValueMappingException = BadRequestException.class)
    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.THROW_EXCEPTION)
    SharedDeckSort mapSort(String sort);

    List<SharedDeckSearchResult> map(List<io.github.mateusz00.entity.SharedDeck> content);
}
