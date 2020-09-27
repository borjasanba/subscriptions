package com.adidas.challenge.internal.util;

import com.adidas.challenge.internal.subscription.Subscription;
import com.adidas.challenge.internal.subscription.dto.SubscriptionDTO;
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
        OBJECT_MAPPER.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    public static <T> EntityDataTest<T> typeOf(Class<T> tClass) {
        EntityDataTest entityDataTest = null;
        if (tClass.equals(SubscriptionDTO.class)) {
            entityDataTest = new SubscriptionDTOGenerator();
        } else if (tClass.equals(Subscription.class)) {
            entityDataTest = new SubscriptionGenerator();
        }
        return entityDataTest;
    }

    public interface EntityDataTest<T> {

        T generateSingle();

    }

}
