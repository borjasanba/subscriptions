package com.adidas.challenge.internal.subscription.mapper;

import com.adidas.challenge.internal.subscription.Subscription;
import com.adidas.challenge.internal.subscription.dto.SubscriptionDTO;
import com.adidas.challenge.internal.subscription.dto.SubscriptionId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SubscriptionMapper {

    SubscriptionMapper SUBSCRIPTION_MAPPER = Mappers.getMapper(SubscriptionMapper.class);

    Subscription dtoToSubscription(final SubscriptionDTO subscriptionDTO);

    @Mapping(target = "id", source = "subscription.id")
    SubscriptionId subscriptionToId(final Subscription subscription);
}
