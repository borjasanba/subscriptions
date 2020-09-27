package com.adidas.challenge.internal.util;

import com.adidas.challenge.internal.subscription.dto.SubscriptionDTO;
import net.andreinc.mockneat.MockNeat;
import net.andreinc.mockneat.unit.objects.Filler;

import java.time.LocalDate;

public class SubscriptionDTOGenerator extends AbstractDataGenerator<SubscriptionDTO> {

    Filler<SubscriptionDTO> filler() {
        MockNeat m = MockNeat.threadLocal();
        return m.filler(SubscriptionDTO::new)
                .setter(SubscriptionDTO::setConsent, m.filler(() -> true))
                .setter(SubscriptionDTO::setGender, m.from(SubscriptionDTO.GenderEnum.values()))
                .setter(SubscriptionDTO::setFirstName, m.names())
                .setter(SubscriptionDTO::setDateOfBirth, m.localDates().between(LocalDate.now().minusYears(80), LocalDate.now().minusYears(18)))
                .setter(SubscriptionDTO::setEmail, m.emails());

    }
}
