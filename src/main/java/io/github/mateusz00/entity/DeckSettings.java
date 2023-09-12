package io.github.mateusz00.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeckSettings
{
    private Integer newCardsPerDay;
    private Float easyRate;
    private Float hardRate;
    private Integer maxInterval;
    private Integer minInterval;
    private Float globalEaseModifier;
    private Float intervalRateAfterFail;
    private Integer leechThreshold;
    private List<Integer> newCardSteps;
}
