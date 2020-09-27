package com.adidas.challenge.email;

import com.adidas.challenge.email.dto.Subscription;
import com.adidas.challenge.email.util.DataGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EmailConsumer.class)
public class EmailConsumerTest {

    @Autowired
    private EmailConsumer emailConsumer;

    @MockBean
    private EmailService emailService;

    @MockBean
    private ObjectMapper objectMapper;

    @MockBean
    private EmailProcessor emailProcessor;

    private TopologyTestDriver testDriver;

    private ConsumerRecordFactory<String, String> recordFactory;

    @Before
    public void setUp() {
        recordFactory = new ConsumerRecordFactory<>(Serdes.String().serializer(), Serdes.String().serializer());

        StreamsBuilder builder = new StreamsBuilder();
        emailConsumer.processNewSubscription(builder.stream("adidas_subscription_test"));

        testDriver = new TopologyTestDriver(builder.build(), getStreamsConfiguration());
    }

    @After
    public void finish() {
        Try.run(() -> testDriver.close());
    }

    private Properties getStreamsConfiguration() {
        Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "email-server-test");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:9999");
        streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, "target");
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, "org.apache.kafka.common.serialization.Serdes$StringSerde");
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, "org.apache.kafka.common.serialization.Serdes$StringSerde");

        return streamsConfiguration;
    }

    @Test
    public void shouldProcessSubscriptionAndSendMail() throws Exception{
        // given
        Subscription subscription = DataGenerator.typeOf(Subscription.class).generateSingle();
        String subscriptionStr = DataGenerator.OBJECT_MAPPER.writeValueAsString(subscription);

        // when
        when(objectMapper.readValue(anyString(), (Class<Object>) any())).thenReturn(subscription);
        testDriver.pipeInput(recordFactory.create("adidas_subscription_test", subscription.getId(), subscriptionStr));

        // then
        verify(emailService, times(1)).sendEmail(any());
    }



}
