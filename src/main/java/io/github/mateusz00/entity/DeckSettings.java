package io.github.mateusz00.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeckSettings
{
    private int newCardsPerDay;
    private float easyRate;
    private float hardRate;
    private int maxInterval;
    private int minIntervalIncrease;
    private float globalEaseModifier;
    private float intervalRateAfterFail;
    private int leechThreshold;
    private int[] newCardSteps;
}
