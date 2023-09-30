package io.github.mateusz00.service;

import javax.annotation.Nullable;

import org.springframework.data.domain.PageRequest;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PageBaseQuery
{
    private final int pageNumber;
    private final int pageSize;
    @Nullable
    private final SortingBase sort;

    public PageRequest getPageRequest()
    {
        if (sort == null)
        {
            return PageRequest.of(pageNumber, pageSize);
        }
        return PageRequest.of(pageNumber, pageSize, sort.toSort());
    }
}
