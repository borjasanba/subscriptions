package com.adidas.challenge.email;

import com.adidas.challenge.email.content.NewSubscriptionEmailContent;
import com.adidas.challenge.email.dto.Subscription;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;

import java.util.Objects;
import java.util.function.Function;

@Slf4j
@EnableBinding(EmailProcessor.class)
@RequiredArgsConstructor
public class EmailConsumer {

    @NonNull
    private ObjectMapper objectMapper;

    @NonNull
    private EmailService emailService;

    @StreamListener
    public void processNewSubscription(@Input("adidas_subscription_source") KStream<String, String> subscriptionStream) {
        subscriptionStream
                .peek((k,v) -> log.debug("received subscription {} {}", k, v))
                .mapValues(mapToSubscription::apply)
                .filter((k,v) -> Objects.nonNull(v))
                .foreach((k,v) -> emailService.sendEmail(templateEngine -> new NewSubscriptionEmailContent(v, templateEngine)));
    }

    private Function <String, Subscription> mapToSubscription = value ->
            Try.of(() -> objectMapper.readValue(value, Subscription.class))
                .onFailure(ex -> {
                    log.error("error reading subscription topic: {}", ex.getMessage());
                }).getOrNull();

}

interface EmailProcessor {

    @Input("adidas_subscription_source")
    KStream<String, String> subscriptionStream();

}
