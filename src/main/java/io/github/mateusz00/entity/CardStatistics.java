package io.github.mateusz00.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardStatistics
{
    @Builder.Default
    private int fails = 0;
    @Builder.Default
    private int reviews = 0;
}
