package io.github.mateusz00.service.deck.shared;

import org.springframework.data.domain.Sort;

import io.github.mateusz00.service.SortingBase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SharedDeckSort implements SortingBase
{
    CARD_COUNT_ASC(Sort.Direction.ASC, DatabaseFields.CARD_COUNT),
    CARD_COUNT_DESC(Sort.Direction.DESC, DatabaseFields.CARD_COUNT),
    POPULARITY_ASC(Sort.Direction.ASC, DatabaseFields.POPULARITY),
    POPULARITY_DESC(Sort.Direction.DESC, DatabaseFields.POPULARITY);

    private final Sort.Direction direction;
    private final String databaseProperty;

    private static class DatabaseFields
    {
        public static final String CARD_COUNT = "cardCount";
        public static final String POPULARITY = "popularity";
    }
}
