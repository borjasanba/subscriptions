package com.adidas.challenge.email.util;

import com.adidas.challenge.email.dto.Subscription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class DataGenerator {

    public static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        OBJECT_MAPPER.registerModule(javaTimeModule);
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        OBJECT_MAPPER.configure(SerializationFeature.INDENT_OUTPUT, false);
    }

    public static <T> EntityDataTest<T> typeOf(Class<T> tClass) {
        EntityDataTest entityDataTest = null;
        if (tClass.equals(Subscription.class)) {
            entityDataTest = new SubscriptionGenerator();
        }
        return entityDataTest;
    }

    public interface EntityDataTest<T> {

        T generateSingle();

    }

}
