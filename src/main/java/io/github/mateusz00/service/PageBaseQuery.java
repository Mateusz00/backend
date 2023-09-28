package io.github.mateusz00.service;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PageBaseQuery
{
    private final int pageNumber;
    private final int pageSize;
}
