package com.denisjava.extended_interactions.util;

import com.denisjava.extended_interactions.impl.EIResultImpl;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashSet;

public class EIResultCollector extends ArrayList<EIResultImpl.Result> {
    private final HashSet<ResourceLocation> addedInteractions;

    public EIResultCollector() {
        this.addedInteractions = new HashSet<>();
    }

    public EIResultCollector(int initialCapacity) {
        super(initialCapacity);
        this.addedInteractions = new HashSet<>(initialCapacity);
    }

    public void offer(EIResultImpl.Result result) {
        if (result instanceof EIResultImpl.Empty) return;
        if (result instanceof EIResultImpl.NonEmptyResult nn) {
            if (addedInteractions.contains(nn.getInteraction().getId())) return;
            addedInteractions.add(nn.getInteraction().getId());
        }
        add(result);
    }
}
