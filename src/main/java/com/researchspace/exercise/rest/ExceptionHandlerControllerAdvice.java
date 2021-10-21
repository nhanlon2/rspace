package com.researchspace.exercise.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(Exception exception) {
        HttpStatus responseStatus = resolveAnnotatedResponseStatus(exception);
        String userErrorMessage = null;
        if (responseStatus == null) {
            responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        if (responseStatus == HttpStatus.INTERNAL_SERVER_ERROR) {
            log.error(makeMessage(exception), exception);
            userErrorMessage = "There is a problem with your request, please contact support.";
        } else {
            log.warn(makeMessage(exception));
            log.debug(makeMessage(exception), exception);
            userErrorMessage = exception.getMessage();
        }
        return new ResponseEntity<>(userErrorMessage, responseStatus);
    }

    private String makeMessage(Exception exception) {
        String message = exception.getMessage();
        Throwable cause = exception.getCause();
        if (cause != null) {
            message += " " + cause.getMessage();
        }
        return message;
    }

    private HttpStatus resolveAnnotatedResponseStatus(Exception exception) {
        ResponseStatus annotation = findMergedAnnotation(exception.getClass(), ResponseStatus.class);
        if (annotation != null) {
            return annotation.value();
        } else {
            return null;
        }
    }

}
