package com.adidas.challenge.internal.subscription.validator;

import com.adidas.challenge.internal.subscription.exception.SubscriptionValidationException;
import com.adidas.challenge.internal.subscription.SubscriptionRepository;
import com.adidas.challenge.internal.subscription.dto.SubscriptionDTO;
import io.vavr.control.Validation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class SubscriptionValidator {

    private final static String EMAIL_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    @NonNull
    private SubscriptionRepository repository;

    public SubscriptionDTO validateCreation(final SubscriptionDTO subscription) {
        // @formatter:off
        return Validation.combine(
                validateNotExist(subscription),
                validateConsent(subscription),
                validateEmail(subscription))
                .ap((v1, v2, v3) -> subscription)
                .fold(errorSeq -> {
                    throw new SubscriptionValidationException(errorSeq.mkString(","));
                }, s -> s);
        // @formatter:off
    }

    private Validation<String, SubscriptionDTO> validateNotExist(final SubscriptionDTO subscription) {
        if (repository.findByEmail(subscription.getEmail()).isPresent()) {
            return Validation.invalid(
                    format("%s already exists for %s", subscription.getClass().getSimpleName(), subscription.getEmail())
            );
        }
        return Validation.valid(subscription);
    }

    private Validation<String, SubscriptionDTO> validateConsent(final SubscriptionDTO subscription) {
        return Optional.ofNullable(subscription.getConsent()).isPresent()
                && subscription.getConsent()
                ? Validation.valid(subscription)
                : Validation.invalid(format("consent must be checked", subscription.getEmail()));
    }

    private Validation<String, SubscriptionDTO> validateEmail(final SubscriptionDTO subscription) {
        return Optional.ofNullable(subscription.getEmail()).isPresent()
                && Pattern.compile(EMAIL_PATTERN).matcher(subscription.getEmail()).matches()
                ? Validation.valid(subscription)
                : Validation.invalid(format("%s e-mail format is invalid", subscription.getEmail()));
    }
}