package com.adidas.challenge.internal.subscription;

import com.adidas.challenge.internal.util.DataGenerator;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
public class SubscriptionRepositoryTest {

    @Autowired
    private SubscriptionRepository repository;

    @Test
    public void shouldSave() {
        // given
        Subscription subscription = DataGenerator.typeOf(Subscription.class).generateSingle();

        // when
        repository.save(subscription);

        // then
        Optional<Subscription> subscriptionFromDB = repository.findById(subscription.getId());
        Assertions.assertThat(subscriptionFromDB).isPresent();
        assertThat(subscriptionFromDB.get()).isEqualTo(subscription);
    }

    @Test
    public void shouldFindByEmail() {
        // given
        Subscription subscription = DataGenerator.typeOf(Subscription.class).generateSingle();
        repository.save(subscription);

        // when
        Optional<Subscription> subscriptionFromDB = repository.findByEmail(subscription.getEmail());

        // then
        Assertions.assertThat(subscriptionFromDB).isPresent();
        assertThat(subscriptionFromDB.get()).isEqualTo(subscription);
    }

}
