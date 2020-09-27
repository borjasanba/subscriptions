package com.adidas.challenge.email;

import com.adidas.challenge.email.content.EmailContent;
import com.adidas.challenge.email.content.NewSubscriptionEmailContent;
import com.adidas.challenge.email.dto.Subscription;
import com.adidas.challenge.email.util.DataGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EmailService.class)
public class EmailServiceTest {

    @Autowired
    private EmailService service;

    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockBean
    private ObjectMapper objectMapper;

    @MockBean
    private SpringTemplateEngine springTemplateEngine;

    @MockBean
    private MimeMessage mimeMessage;

    @Test
    public void shouldSendNewSubscriptionEmail() {
        // given
        Subscription subscription = DataGenerator.typeOf(Subscription.class).generateSingle();

        // when
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        service.sendEmail(templateEngine  -> new NewSubscriptionEmailContent(subscription, templateEngine));

        // then
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    public void shouldSendToErrorTopicWhenMailServerFails() throws Exception{
        // given
        Subscription subscription = DataGenerator.typeOf(Subscription.class).generateSingle();
        String subscriptionStr = DataGenerator.OBJECT_MAPPER.writeValueAsString(subscription);

        // when
        when(objectMapper.readValue(anyString(), (Class<Object>) any())).thenReturn(subscription);
        when(objectMapper.writeValueAsString(any(EmailContent.class))).thenReturn(subscriptionStr);
        doThrow(new RuntimeException()).when(javaMailSender).send(any(SimpleMailMessage.class));
        service.sendEmail(templateEngine  -> new NewSubscriptionEmailContent(subscription, templateEngine));

        // then
        verify(kafkaTemplate, times(1)).send(anyString(), anyString());
    }
}
