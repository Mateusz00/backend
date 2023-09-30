package io.github.mateusz00.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import io.github.mateusz00.api.model.SharedDeck;
import io.github.mateusz00.api.model.SharedDeckCreateRequest;
import io.github.mateusz00.api.model.SharedDeckUpdateRequest;

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
}
