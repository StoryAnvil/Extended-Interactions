package com.denisjava.extended_interactions.util;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;

import java.util.*;
import java.util.stream.Stream;

public class EIProviderRegistry<PROVIDER> {
    public static final Identifier ALL = Identifier.withDefaultNamespace("all");
    private final HashMap<Identifier, ArrayList<PROVIDER>> data = new HashMap<>();
    private boolean frozen = false;

    public void register(Identifier subject, PROVIDER provider) {
        if (frozen) throw new IllegalStateException("EIProviderRegistry is frozen already! You are registering providers too late.");

        ArrayList<PROVIDER> providers = data.computeIfAbsent(subject, u -> new ArrayList<>(1));
        providers.add(provider);
    }
    public void registerToTag(Identifier subjectTag, PROVIDER provider) {
        register(Identifier.fromNamespaceAndPath("tags", subjectTag.getNamespace() + '/' + subjectTag.getPath()), provider);
    }

    public Iterable<PROVIDER> listAll(Identifier subject) {
        ArrayList<PROVIDER> providers = data.get(subject);
        if (providers == null) return List.of();
        return providers;
    }

    public <T> Stream<Iterable<PROVIDER>> listAll(Identifier subject, Stream<TagKey<T>> tags) {
        return Stream.concat(Stream.of(subject, ALL), tags.map(EIProviderRegistry::keyToIdentifier)).map(this::listAll);
    }

    private static <T> Identifier keyToIdentifier(TagKey<T> t) {
        return Identifier.fromNamespaceAndPath("tags", t.location().getNamespace() + '/' + t.location().getPath());
    }

    public void freeze() {
        if (frozen) throw new IllegalStateException("EIProviderRegistry is frozen already!");
        frozen = true;
        data.values().forEach(ArrayList::trimToSize);
    }
}
