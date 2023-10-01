package io.github.mateusz00.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeckReviewStatisticsSummary
{
    private int hard = 0;
    private int normal = 0;
    private int easy = 0;
    private int wrong = 0;
    private int suspended = 0;
}
