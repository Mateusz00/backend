package io.github.mateusz00.service;

import org.springframework.data.domain.Sort;

import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.NotNull;

public interface SortingBase
{
    @Nullable
    Sort.Direction getDirection();

    @NotNull
    String getDatabaseProperty();

    @NotNull
    default Sort toSort()
    {
        return Sort.by(new Sort.Order(getDirection(), getDatabaseProperty()));
    }
}
