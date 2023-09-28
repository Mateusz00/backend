package io.github.mateusz00.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.mateusz00.exception.ApiErrorResponse;
import io.github.mateusz00.exception.AuthorizationException;
import io.github.mateusz00.exception.BadRequestException;
import io.github.mateusz00.exception.InternalException;
import io.github.mateusz00.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalRestExceptionHandler
{
    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public ResponseEntity<ApiErrorResponse> exceptionHandler(MethodArgumentNotValidException e) {
        var msg = Optional.of(e)
                .map(BindException::getBindingResult)
                .map(Errors::getFieldErrors)
                .stream()
                .flatMap(Collection::stream)
                .map(objectError -> objectError.getField() + "=" + objectError.getDefaultMessage())
                .collect(Collectors.joining(". "));
        var apiErrorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), msg);
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ AuthorizationException.class })
    public ResponseEntity<ApiErrorResponse> genericHandler403(Exception e) {
        var apiErrorResponse = new ApiErrorResponse(HttpStatus.FORBIDDEN.value(), "You must be authorized");
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({ NotFoundException.class })
    public ResponseEntity<ApiErrorResponse> genericHandler404(Exception e) {
        var apiErrorResponse = new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ BadRequestException.class, UsernameNotFoundException.class })
    public ResponseEntity<ApiErrorResponse> exceptionHandlerBadRequest(BadRequestException e) {
        var apiErrorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ InternalException.class, IOException.class })
    public ResponseEntity<ApiErrorResponse> exceptionHandlerInternal(Exception e) {
        log.error("Internal exception occurred: {}", e.getMessage(), e);
        return getInternalErrorResponse();
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<ApiErrorResponse> exceptionHandlerUncaught(Exception e) {
        log.error("Unexpected exception occurred: {}", e.getMessage(), e);
        return getInternalErrorResponse();
    }

    private ResponseEntity<ApiErrorResponse> getInternalErrorResponse()
    {
        var apiErrorResponse = new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error");
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
