package io.github.mateusz00.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeckReviewStatisticsEntry
{
    private int day;
    private int hard;
    private int normal;
    private int easy;
    private int wrong;
    private int suspended;
}
