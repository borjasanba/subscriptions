package com.adidas.challenge.internal.subscription.validator;

import com.adidas.challenge.internal.subscription.exception.SubscriptionValidationException;
import com.adidas.challenge.internal.subscription.dto.SubscriptionDTO;
import com.adidas.challenge.internal.subscription.Subscription;
import com.adidas.challenge.internal.subscription.SubscriptionRepository;
import com.adidas.challenge.internal.util.DataGenerator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SubscriptionValidator.class)
public class SubscriptionValidatorTest {

    @Autowired
    private SubscriptionValidator validator;

    @MockBean
    private SubscriptionRepository repository;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldValidateSubscription() {
        // given
        SubscriptionDTO subscription = DataGenerator.typeOf(SubscriptionDTO.class).generateSingle();

        // when
        validator.validateCreation(subscription);

        // then
        verify(repository, times(1)).findByEmail(anyString());
    }

    @Test
    public void shouldInvalidateWhenExistsEmail() {
        // given
        expectedException.expect(SubscriptionValidationException.class);
        expectedException.expectMessage("already exists for");
        SubscriptionDTO subscription = DataGenerator.typeOf(SubscriptionDTO.class).generateSingle();

        // when
        when(repository.findByEmail(anyString())).thenReturn(Optional.of(new Subscription()));
        validator.validateCreation(subscription);
    }

    @Test
    public void shouldInvalidateWhenInvalidEmail() {
        // given
        expectedException.expect(SubscriptionValidationException.class);
        expectedException.expectMessage("e-mail format is invalid");
        SubscriptionDTO subscription = DataGenerator.typeOf(SubscriptionDTO.class).generateSingle();
        subscription.setEmail("InvalidEmail");

        // when
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
        validator.validateCreation(subscription);
    }

    @Test
    public void shouldInvalidateWhenConsentNotChecked() {
        // given
        expectedException.expect(SubscriptionValidationException.class);
        expectedException.expectMessage("consent must be checked");
        SubscriptionDTO subscription = DataGenerator.typeOf(SubscriptionDTO.class).generateSingle();
        subscription.setConsent(false);

        // when
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
        validator.validateCreation(subscription);
    }
}
