package com.adidas.challenge.internal.subscription;

import com.adidas.challenge.internal.subscription.dto.SubscriptionDTO;
import com.adidas.challenge.internal.subscription.dto.SubscriptionId;
import com.adidas.challenge.internal.subscription.exception.SubscriptionPersistenceException;
import com.adidas.challenge.internal.subscription.exception.SubscriptionValidationException;
import com.adidas.challenge.internal.subscription.validator.SubscriptionValidator;
import com.adidas.challenge.internal.util.DataGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = SubscriptionController.class)
public class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscriptionService service;

    @MockBean
    private SubscriptionValidator validator;

    @Test
    public void shouldHaveEndpointToCreateSubscription() throws Exception {
        // given
        SubscriptionDTO subscription = DataGenerator.typeOf(SubscriptionDTO.class).generateSingle();
        String subscriptionJson = DataGenerator.OBJECT_MAPPER.writeValueAsString(subscription);
        SubscriptionId subscriptionId = new SubscriptionId();
        subscriptionId.setId("DUMMY_ID");

        // when & then
        when(service.createSubscription(any(SubscriptionDTO.class))).thenReturn(subscriptionId);
        when(validator.validateCreation(any(SubscriptionDTO.class))).thenReturn(subscription);
        mockMvc.perform(post("/api/subscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(subscriptionJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("DUMMY_ID"));
    }

    @Test
    public void shouldHandleValidationException() throws Exception {
        // given
        SubscriptionDTO subscription = DataGenerator.typeOf(SubscriptionDTO.class).generateSingle();
        String subscriptionJson = DataGenerator.OBJECT_MAPPER.writeValueAsString(subscription);

        // when & then
        when(validator.validateCreation(any(SubscriptionDTO.class))).thenThrow(new SubscriptionValidationException("validation error"));
        mockMvc.perform(post("/api/subscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(subscriptionJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("validation error"));
    }

    @Test
    public void shouldHandlePersistenceException() throws Exception {
        // given
        SubscriptionDTO subscription = DataGenerator.typeOf(SubscriptionDTO.class).generateSingle();
        String subscriptionJson = DataGenerator.OBJECT_MAPPER.writeValueAsString(subscription);

        // when & then
        when(validator.validateCreation(any(SubscriptionDTO.class))).thenThrow(new SubscriptionPersistenceException("persistence error"));
        mockMvc.perform(post("/api/subscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(subscriptionJson))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("persistence error"));
    }

}
