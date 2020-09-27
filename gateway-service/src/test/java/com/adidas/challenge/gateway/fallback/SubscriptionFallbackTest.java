package com.adidas.challenge.gateway.fallback;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.ConnectException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SubscriptionFallbackTest {

    @Autowired
    private SubscriptionFallback fallback;

    @Test
    public void shouldHandleFallBack() throws IOException {
        // given
        ConnectException exception = new ConnectException("Connection refused");

        // when
        ClientHttpResponse response = fallback.fallbackResponse("subscription-service", exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(IOUtils.toString(response.getBody())).isEqualTo("Subscription service is not available");
        assertThat(response.getRawStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getStatusText()).isEqualTo("Internal Server Error");
        assertThat(response.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0).toString()).isEqualTo(MediaType.APPLICATION_JSON.toString());
    }

}
