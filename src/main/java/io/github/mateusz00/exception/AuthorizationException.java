package io.github.mateusz00.exception;

public class AuthorizationException extends RuntimeException
{
    public AuthorizationException()
    {
        super();
    }

    public AuthorizationException(String message)
    {
        super(message);
    }

    public AuthorizationException(String message, Throwable t)
    {
        super(message, t);
    }
}
