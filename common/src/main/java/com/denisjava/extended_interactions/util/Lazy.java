package com.denisjava.extended_interactions.util;

import java.util.function.Supplier;

public class Lazy<T> implements Supplier<T> {
    private Supplier<T> sup;
    private T value;

    public Lazy(Supplier<T> sup) {
        this.sup = sup;
    }

    @Override
    public T get() {
        if (sup != null) {
            value = sup.get();
            sup = null;
        }
        return value;
    }
}
