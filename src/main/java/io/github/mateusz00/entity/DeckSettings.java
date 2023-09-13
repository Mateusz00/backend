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
    private int newCardsPerDay;
    private float easyRate;
    private float hardRate;
    private int maxInterval;
    private int minInterval;
    private float globalEaseModifier;
    private float intervalRateAfterFail;
    private int leechThreshold;
    private List<Integer> newCardSteps;
}
