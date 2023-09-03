package io.github.mateusz00.exception;

public class BadRequestException extends RuntimeException
{
    public BadRequestException(String message)
    {
        super(message);
    }

    public BadRequestException(String message, Throwable t)
    {
        super(message, t);
    }
}
