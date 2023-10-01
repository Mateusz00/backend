package io.github.mateusz00.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

/**
 * For easier mocking in tests
 */
@Service
public class LocalDateTimeProvider
{
    public LocalDateTime now()
    {
        return LocalDateTime.now();
    }
}
