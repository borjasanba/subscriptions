package com.adidas.challenge.internal.subscription;

import com.adidas.challenge.internal.subscription.exception.SubscriptionPersistenceException;
import com.adidas.challenge.internal.subscription.dto.SubscriptionDTO;
import com.adidas.challenge.internal.subscription.dto.SubscriptionId;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;

import static com.adidas.challenge.internal.subscription.mapper.SubscriptionMapper.SUBSCRIPTION_MAPPER;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    public static final String ADIDAS_SUBSCRIPTION_TOPIC = "adidas_subscription";

    @NonNull
    private SubscriptionRepository repository;

    @NonNull
    private KafkaTemplate<String, String> kafkaTemplate;

    @NonNull
    private ObjectMapper objectMapper;

    public SubscriptionId createSubscription(final SubscriptionDTO subscriptionDTO) {
        return Function.<SubscriptionDTO>identity()
                .andThen(SUBSCRIPTION_MAPPER::dtoToSubscription)
                .andThen(this::saveSubscription)
                .andThen(this::sendSubscriptionToTopic)
                .andThen(SUBSCRIPTION_MAPPER::subscriptionToId)
                .apply(subscriptionDTO);
    }

    private Subscription saveSubscription(final Subscription subscription) {
        subscription.setId(UUID.randomUUID().toString());
        subscription.setCreatedAt(LocalDateTime.now());

        return Try.of(() -> repository.save(subscription))
                .onSuccess(s -> log.debug("subscription saved {}", subscription))
                .onFailure(ex -> log.error("error persisting {}", subscription))
                .getOrElseThrow((ex) -> new SubscriptionPersistenceException(ex.getMessage()));
    }

    private Subscription sendSubscriptionToTopic(final Subscription subscription) {
        Try.of(() -> kafkaTemplate.send(ADIDAS_SUBSCRIPTION_TOPIC, subscription.getId(), objectMapper.writeValueAsString(subscription)))
                .onSuccess(s -> log.debug("subscription sent to topic {}", subscription))
                .onFailure(ex -> log.error("error sending subscription to topic {}", subscription));

        return subscription;
    }

}
