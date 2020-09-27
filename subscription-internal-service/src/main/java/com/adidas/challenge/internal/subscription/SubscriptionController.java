package com.adidas.challenge.internal.subscription;

import com.adidas.challenge.internal.subscription.validator.SubscriptionValidator;
import com.adidas.challenge.internal.subscription.api.SubscriptionApi;
import com.adidas.challenge.internal.subscription.dto.SubscriptionDTO;
import com.adidas.challenge.internal.subscription.dto.SubscriptionId;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.function.Function;

@RestController
@RequiredArgsConstructor
public class SubscriptionController implements SubscriptionApi {

    @NonNull
    private SubscriptionService service;

    @NonNull
    private SubscriptionValidator validator;

    @Override
    public ResponseEntity<SubscriptionId> createSubscription(@Valid @RequestBody SubscriptionDTO subscription) {
        return ResponseEntity.ok(Function.<SubscriptionDTO>identity()
                .andThen(validator::validateCreation)
                .andThen(service::createSubscription)
                .apply(subscription)
        );
    }
}
