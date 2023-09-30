package io.github.mateusz00.service.deck;

import org.springframework.data.domain.Sort;

import io.github.mateusz00.service.SortingBase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CardSort implements SortingBase
{
    NEXT_REVIEW_ASC(Sort.Direction.ASC, Constants.NEXT_REVIEW),
    NEXT_REVIEW_DESC(Sort.Direction.DESC, Constants.NEXT_REVIEW),
    INTERVAL_ASC(Sort.Direction.ASC, Constants.INTERVAL),
    INTERVAL_DESC(Sort.Direction.DESC, Constants.INTERVAL);

    private final Sort.Direction direction;
    private final String databaseProperty;

    private static class Constants
    {
        public static final String INTERVAL = "interval";
        public static final String NEXT_REVIEW = "nextReview";
    }
}
