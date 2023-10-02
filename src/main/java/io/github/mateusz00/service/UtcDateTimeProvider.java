package io.github.mateusz00.service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

@Service
public class UtcDateTimeProvider
{
    public LocalDateTime now()
    {
        return LocalDateTime.now(Clock.systemUTC());
    }

    public LocalDateTime convert(Instant instant)
    {
        return LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }

    public Instant toInstant(LocalDateTime dateTime)
    {
        return dateTime.atOffset(ZoneOffset.UTC).toInstant();
    }
}
