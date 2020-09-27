package com.adidas.challenge.email;

import com.adidas.challenge.email.content.EmailContent;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    public static final String EMAIL_SENDER = "noreply@adidas.com";
    @Value("${email.service.active:false}")
    private Boolean active;

    public static final String DLQ_EMAIL_TOPIC = "dlq_adidas_mail";

    @NonNull
    private KafkaTemplate<String, String> kafkaTemplate;

    @NonNull
    private JavaMailSender emailSender;

    @NonNull
    private ObjectMapper objectMapper;

    @NonNull
    private SpringTemplateEngine templateEngine;

    @Async("mailExecutor")
    public void sendEmail(final Function<SpringTemplateEngine, EmailContent> mailContent) {
        EmailContent mail = mailContent.apply(templateEngine);
        Try.run(() -> {
            MimeMessage message = createEmailMessage(mail);
            emailSender.send(message);
        }).onFailure(ex -> {
            log.error("error sending e-mail: {}", ex.getMessage());
            kafkaTemplate.send(DLQ_EMAIL_TOPIC,
                    Try.of(() -> objectMapper.writeValueAsString(mail)).getOrNull());
        });;
    }

    private MimeMessage createEmailMessage(final EmailContent emailContentMail) throws MessagingException {
        MimeMessage msg = emailSender.createMimeMessage();

        String recipients = active ? emailContentMail.getRecipients() : "test@adidas.com";
        String body = emailContentMail.getBody();

        msg.setFrom(EMAIL_SENDER);
        msg.setRecipients(Message.RecipientType.TO, recipients);
        msg.setSubject(emailContentMail.getSubject());
        msg.setContent(body, "text/html; charset=utf-8");
        log.debug("sending email with subject {} to {}", recipients, msg.getSubject());

        return msg;
    }

}
