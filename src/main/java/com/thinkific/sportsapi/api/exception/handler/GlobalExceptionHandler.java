package com.thinkific.sportsapi.api.exception.handler;

import com.thinkific.sportsapi.api.exception.AlreadyExistsException;
import com.thinkific.sportsapi.api.exception.CantSignupUserException;
import com.thinkific.sportsapi.api.exception.domain.ErrorInfo;
import com.thinkific.sportsapi.api.exception.domain.Violation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(CantSignupUserException.class)
    @ResponseStatus(SERVICE_UNAVAILABLE)
    public @ResponseBody ErrorInfo onCantSignupUserException(final CantSignupUserException exception) {
        return mapErrorInfo(
                SERVICE_UNAVAILABLE,
                exception.getMessage()
        );
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorInfo onAlreadyExistsException(final AlreadyExistsException exception) {

        return mapErrorInfo(
                BAD_REQUEST,
                exception.getMessage()
        );
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(BAD_REQUEST)
    public @ResponseBody ErrorInfo onBindException(final WebExchangeBindException bindViolation) {

        final List<Violation> violationList = bindViolation
                .getFieldErrors()
                .stream()
                .map(violation -> mapViolation(violation.getField(), violation.getDefaultMessage()))
                .collect(Collectors.toList());

        return mapErrorInfo(
                BAD_REQUEST,
                bindViolation.getBindingResult().getObjectName(),
                violationList
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(SERVICE_UNAVAILABLE)
    public @ResponseBody ErrorInfo onException(final Exception exception) {

        return mapErrorInfo(
                SERVICE_UNAVAILABLE,
                SERVICE_UNAVAILABLE.getReasonPhrase()
        );
    }

    private Violation mapViolation(final String fieldName, final String message){
        return new Violation(fieldName, message);
    }
    private ErrorInfo mapErrorInfo(HttpStatus status, String message){
        return new ErrorInfo(
                Instant.now(),
                status.value(),
                status.name(),
                message,
                null
        );
    }
    private ErrorInfo mapErrorInfo(HttpStatus status, String message, List<Violation> errors){
        return new ErrorInfo(
                Instant.now(),
                status.value(),
                status.name(),
                message,
                errors
        );
    }

}
