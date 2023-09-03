package io.github.mateusz00.exception;

public class InternalException extends RuntimeException
{
    public InternalException(String message)
    {
        super(message);
    }

    public InternalException(String message, Throwable t)
    {
        super(message, t);
    }
}
