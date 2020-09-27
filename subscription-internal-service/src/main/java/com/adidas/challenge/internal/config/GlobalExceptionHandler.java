package com.adidas.challenge.internal.config;

import com.adidas.challenge.internal.subscription.exception.SubscriptionPersistenceException;
import com.adidas.challenge.internal.subscription.exception.SubscriptionValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SubscriptionValidationException.class)
    public ResponseEntity<Object> handleValidationException(final SubscriptionValidationException exception) {
        return ResponseEntity.badRequest().body(Collections.singletonMap("message", exception.getMessage()));
    }

    @ExceptionHandler(SubscriptionPersistenceException.class)
    public ResponseEntity<Object> handlePersistenceException(final SubscriptionPersistenceException exception) {
        return new ResponseEntity(Collections.singletonMap("message", exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
