package io.github.mateusz00.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardStatistics
{
    private int fails = 0;
    private int reviews = 0;
}
