package io.github.mateusz00.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(
        name = "unique_deck_review_statistics",
        def = "{'deckId': 1, 'year': 1, 'month': 1}",
        unique = true
)
public class DeckReviewStatistics
{
    @Id
    private String id;
    private String deckId;
    private int year;
    private int month;
    private DeckReviewStatisticsEntry monthSummary;
    private List<DeckReviewStatisticsEntry> reviews;
}
