package io.github.mateusz00.service.deck;

import org.springframework.data.domain.Sort;

import io.github.mateusz00.service.SortingBase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CardSort implements SortingBase
{
    NEXT_REVIEW_ASC(Sort.Direction.ASC, DatabaseFields.NEXT_REVIEW),
    NEXT_REVIEW_DESC(Sort.Direction.DESC, DatabaseFields.NEXT_REVIEW),
    INTERVAL_ASC(Sort.Direction.ASC, DatabaseFields.INTERVAL),
    INTERVAL_DESC(Sort.Direction.DESC, DatabaseFields.INTERVAL);

    private final Sort.Direction direction;
    private final String databaseProperty;

    private static class DatabaseFields
    {
        public static final String INTERVAL = "interval";
        public static final String NEXT_REVIEW = "nextReview";
    }
}
