package io.github.mateusz00.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.github.mateusz00.entity.Card;
import io.github.mateusz00.entity.SharedCard;

@Mapper
public interface SharedCardMapper
{
    @Mapping(target = "id", ignore = true)
    SharedCard map(Card card, String sharedDeckId);

    List<io.github.mateusz00.api.model.SharedCard> mapCards(List<SharedCard> content);
}
