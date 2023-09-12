package io.github.mateusz00.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeckReviewStatisticsEntry
{
    private int hard;
    private int normal;
    private int easy;
    private int wrong;
    private int suspended;
}
