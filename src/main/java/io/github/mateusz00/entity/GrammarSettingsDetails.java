package io.github.mateusz00.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrammarSettingsDetails
{
    private int maxInterval;
    private float intervalRateAfterFail;
    private int failThreshold;
    private int exercisesPerDay;
}
