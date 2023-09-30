package io.github.mateusz00.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomGrammarSettingsDetails
{
    private Integer maxInterval;
    private Float intervalRateAfterFail;
    private Integer failThreshold;
    private Integer exercisesPerDay;
}
