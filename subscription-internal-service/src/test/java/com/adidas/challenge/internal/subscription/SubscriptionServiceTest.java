package com.adidas.challenge.internal.subscription;

import com.adidas.challenge.internal.subscription.dto.SubscriptionDTO;
import com.adidas.challenge.internal.subscription.dto.SubscriptionId;
import com.adidas.challenge.internal.util.DataGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SubscriptionService.class)
public class SubscriptionServiceTest {

    @Autowired
    private SubscriptionService service;

    @MockBean
    private SubscriptionRepository repository;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockBean
    private ObjectMapper mapper;

    @Test
    public void shouldCreateSubscription() {
        // given
        SubscriptionDTO subscription = DataGenerator.typeOf(SubscriptionDTO.class).generateSingle();

        // when
        when(repository.save(any(Subscription.class))).then(invocation -> invocation.getArgument(0));
        SubscriptionId subscriptionId = service.createSubscription(subscription);

        // then
        assertThat(subscriptionId.getId()).isNotNull();
        verify(repository, times(1)).save(any(Subscription.class));
        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), any());
    }
}
