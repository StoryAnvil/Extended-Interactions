package com.denisjava.extended_interactions.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.*;
import java.util.stream.Stream;

public class EIProviderRegistry<PROVIDER> {
    private final HashMap<ResourceLocation, ArrayList<PROVIDER>> data = new HashMap<>();
    private boolean frozen = false;

    public void register(ResourceLocation subject, PROVIDER provider) {
        if (frozen) throw new IllegalStateException("EIProviderRegistry is frozen already! You are registering providers too late.");

        ArrayList<PROVIDER> providers = data.computeIfAbsent(subject, u -> new ArrayList<>(1));
        providers.add(provider);
    }
    public void registerToTag(ResourceLocation subjectTag, PROVIDER provider) {
        register(ResourceLocation.fromNamespaceAndPath("tags", subjectTag.getNamespace() + '/' + subjectTag.getPath()), provider);
    }

    public Iterable<PROVIDER> listAll(ResourceLocation subject) {
        ArrayList<PROVIDER> providers = data.get(subject);
        if (providers == null) return List.of();
        return providers;
    }

    public <T> Stream<Iterable<PROVIDER>> listAll(ResourceLocation subject, Stream<TagKey<T>> tags) {
        return Stream.concat(Stream.of(subject), tags.map(EIProviderRegistry::keyToResourceLocation)).map(this::listAll);
    }

    private static <T> ResourceLocation keyToResourceLocation(TagKey<T> t) {
        return ResourceLocation.fromNamespaceAndPath("tags", t.location().getNamespace() + t.location().getPath());
    }

    public void freeze() {
        if (frozen) throw new IllegalStateException("EIProviderRegistry is frozen already!");
        frozen = true;
        data.values().forEach(ArrayList::trimToSize);
    }
}
