package com.adidas.challenge.email.content;

import com.adidas.challenge.email.dto.Subscription;
import lombok.AllArgsConstructor;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.Optional;

@AllArgsConstructor
public class NewSubscriptionEmailContent implements EmailContent {

    private Subscription subscription;
    private SpringTemplateEngine templateEngine;

    @Override
    public String getRecipients() {
        return subscription.getEmail();
    }

    @Override
    public String getSubject() {
        return "Welcome to Adidas";
    }

    @Override
    public String getBody() {
        Context ctx = new Context();
        ctx.setVariable("firstName", getFirstName());
        return getPlainBody();
        //return templateEngine.process("newsletter-subscription.html", ctx); TODO solve context injection
    }

    @Override
    public String getPlainBody() {
        return String.format("Welcome {}!. You have successfully subscribed to Adidas Newsletter and you will never ever want to unsubscribe :)", getFirstName());
    }

    private String getFirstName() {
        return Optional.ofNullable(subscription.getFirstName()).orElse("Adidas Fan");
    }
}
