package io.github.mateusz00.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomDeckSettings
{
    private Integer newCardsPerDay;
    private Float easyRate;
    private Float hardRate;
    private Integer maxInterval;
    private Integer minIntervalIncrease;
    private Float globalEaseModifier;
    private Float intervalRateAfterFail;
    private Integer leechThreshold;
    private List<Integer> newCardSteps;
}
