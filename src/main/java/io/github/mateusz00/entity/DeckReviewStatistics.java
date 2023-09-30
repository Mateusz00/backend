package io.github.mateusz00.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@CompoundIndex(
        name = "unique_deck_review_statistics",
        def = "{'deckId': 1, 'year': 1, 'month': 1}",
        unique = true
)
@TypeAlias("DeckReviewStatistics")
public class DeckReviewStatistics
{
    @Id
    private String id;
    private String deckId;
    private int year;
    private int month;
    @Nullable
    private DeckReviewStatisticsSummary monthSummary;
    @Nullable
    private List<DeckReviewStatisticsEntry> reviews;
}
