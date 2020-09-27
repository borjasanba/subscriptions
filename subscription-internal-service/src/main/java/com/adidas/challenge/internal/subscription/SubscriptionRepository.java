package com.adidas.challenge.internal.subscription;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, String> {

    Optional<Subscription> findByEmail(final String email);

}
