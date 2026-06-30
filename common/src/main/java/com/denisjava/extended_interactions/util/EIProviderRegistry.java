package com.denisjava.extended_interactions.util;

import java.util.*;

public class EIProviderRegistry<SUBJECT, PROVIDER> {
    private final HashMap<SUBJECT, ArrayList<PROVIDER>> data = new HashMap<>();
    private boolean frozen = false;

    public Iterable<PROVIDER> listAll(SUBJECT subject) {
        ArrayList<PROVIDER> providers = data.get(subject);
        if (providers == null) return List.of();
        return providers;
    }

    public void register(SUBJECT subject, PROVIDER provider) {
        if (frozen) throw new IllegalStateException("EIProviderRegistry is frozen already! You are registering providers too late.");

        ArrayList<PROVIDER> providers = data.computeIfAbsent(subject, u -> new ArrayList<>(1));
        providers.add(provider);
    }

    public void freeze() {
        if (frozen) throw new IllegalStateException("EIProviderRegistry is frozen already!");
        frozen = true;
        data.values().forEach(ArrayList::trimToSize);
    }
}
