package com.adidas.challenge.internal.util;

import net.andreinc.mockneat.unit.objects.Filler;

public abstract class AbstractDataGenerator <T> implements DataGenerator.EntityDataTest<T> {

    @Override
    public T generateSingle() {
        return filler().val();
    }

    abstract Filler<T> filler();

}
