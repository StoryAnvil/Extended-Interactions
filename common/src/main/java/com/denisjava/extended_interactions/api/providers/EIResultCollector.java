package com.denisjava.extended_interactions.api.providers;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.List;

/**
 * Instances of this class are used to collect {@link EIResult}s from collectors
 */
public class EIResultCollector {
    private final List<EIResult> collector;

    @ApiStatus.Internal
    public EIResultCollector(List<EIResult> collector) {
        this.collector = collector;
    }

    public void add(EIResult result) {
        collector.add(result);
    }

    public void addAll(Collection<? extends EIResult> results) {
        collector.addAll(results);
    }
}
