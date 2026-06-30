package com.denisjava.extended_interactions.api;

import com.denisjava.extended_interactions.impl.ExtendedInteractionsImpl;
import org.jetbrains.annotations.ApiStatus;

public class InteractionRegistrar {
    @ApiStatus.Internal
    public InteractionRegistrar() {}

    public void register(ExtInteraction interaction) {
        ExtendedInteractionsImpl.registerInteraction(interaction);
    }
}
