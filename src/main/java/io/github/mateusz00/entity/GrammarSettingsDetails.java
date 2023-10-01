package io.github.mateusz00.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class GrammarSettingsDetails
{
    private int maxInterval;
    private float intervalRateAfterFail;
    private int failThreshold;
    private int exercisesPerDay;
}
