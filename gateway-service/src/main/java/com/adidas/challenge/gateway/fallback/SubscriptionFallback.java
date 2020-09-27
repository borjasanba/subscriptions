package com.adidas.challenge.gateway.fallback;

import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionFallback implements FallbackProvider {

    private static final String DEFAULT_SUBSCRITION_MESSAGE = "Subscription service is not available";

    @Override
    public String getRoute() {
        return "subscription-service";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        return new GatewayClientResponse(HttpStatus.INTERNAL_SERVER_ERROR, DEFAULT_SUBSCRITION_MESSAGE);
    }
}
