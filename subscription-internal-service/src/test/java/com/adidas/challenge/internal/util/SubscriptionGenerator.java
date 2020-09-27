package com.adidas.challenge.internal.util;

import com.adidas.challenge.internal.subscription.Subscription;
import net.andreinc.mockneat.MockNeat;
import net.andreinc.mockneat.unit.objects.Filler;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SubscriptionGenerator extends AbstractDataGenerator<Subscription> {

    Filler<Subscription> filler() {
        MockNeat m = MockNeat.threadLocal();
        return m.filler(Subscription::new)
                .setter(Subscription::setId, m.uuids())
                .setter(Subscription::setConsent, m.bools())
                .setter(Subscription::setGender, m.genders())
                .setter(Subscription::setFirstName, m.names())
                .setter(Subscription::setDateOfBirth, m.localDates().between(LocalDate.now().minusYears(80), LocalDate.now().minusYears(18)))
                .setter(Subscription::setCreatedAt, m.filler(() -> LocalDateTime.now()))
                .setter(Subscription::setEmail, m.emails());

    }
}
