package com.thinkific.sportsapi.api.exception.handler;

import com.thinkific.sportsapi.api.exception.AlreadyExistsException;
import com.thinkific.sportsapi.api.exception.CantSignupUserException;
import com.thinkific.sportsapi.api.exception.NoContentException;
import com.thinkific.sportsapi.api.exception.NotFoundException;
import com.thinkific.sportsapi.api.exception.domain.ErrorInfo;
import com.thinkific.sportsapi.api.exception.domain.Violation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityResultHandler;
import org.springframework.web.server.ServerWebInputException;

import java.time.Instant;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class GlobalExceptionHandler  {
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(CantSignupUserException.class)
    @ResponseStatus(SERVICE_UNAVAILABLE)
    public @ResponseBody
    ErrorInfo onCantSignupUserException(final CantSignupUserException exception) {
        return mapErrorInfo(
                SERVICE_UNAVAILABLE,
                exception.getMessage()
        );
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public @ResponseBody
    ErrorInfo onNotFoundException(final NotFoundException exception) {

        return mapErrorInfo(
                NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(NoContentException.class)
    @ResponseStatus(NO_CONTENT)
    public @ResponseBody
    void onNoContentException(final NoContentException exception) {
        log.trace("No content for {}", exception.getMessage());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(BAD_REQUEST)
    public @ResponseBody
    ErrorInfo onAlreadyExistsException(final AlreadyExistsException exception) {

        return mapErrorInfo(
                BAD_REQUEST,
                exception.getMessage()
        );
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(BAD_REQUEST)
    public @ResponseBody
    ErrorInfo onBindException(final WebExchangeBindException bindViolation) {

        final List<Violation> violationList = bindViolation
                .getFieldErrors()
                .stream()
                .map(violation -> mapViolation(violation.getField(), violation.getDefaultMessage()))
                .toList();

        return mapErrorInfo(
                BAD_REQUEST,
                bindViolation.getBindingResult().getObjectName(),
                violationList
        );
    }

    @ExceptionHandler(ServerWebInputException.class)
    @ResponseStatus(BAD_REQUEST)
    public @ResponseBody
    ErrorInfo handle(final ServerWebInputException exception) {
        return mapErrorInfo(
                BAD_REQUEST,
                exception.getReason()
        );

    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(SERVICE_UNAVAILABLE)
    public @ResponseBody
    ErrorInfo onException(final Exception exception) {
        log.trace("Got a {} returning {}", exception.getMessage(), SERVICE_UNAVAILABLE.getReasonPhrase());

        return mapErrorInfo(
                SERVICE_UNAVAILABLE,
                SERVICE_UNAVAILABLE.getReasonPhrase()
        );
    }

    private Violation mapViolation(final String fieldName, final String message) {
        return new Violation(fieldName, message);
    }

    private ErrorInfo mapErrorInfo(HttpStatus status, String message) {
        return new ErrorInfo(
                Instant.now(),
                status.value(),
                status.name(),
                message,
                null
        );
    }

    private ErrorInfo mapErrorInfo(HttpStatus status, String message, List<Violation> errors) {
        return new ErrorInfo(
                Instant.now(),
                status.value(),
                status.name(),
                message,
                errors
        );
    }

}
