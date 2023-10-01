package io.github.mateusz00.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeckReviewStatisticsEntry
{
    private int day = 0;
    private int hard = 0;
    private int normal = 0;
    private int easy = 0;
    private int wrong = 0;
    private int suspended = 0;
}
