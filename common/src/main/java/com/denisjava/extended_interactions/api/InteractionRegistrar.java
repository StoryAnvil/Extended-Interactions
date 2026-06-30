package com.denisjava.extended_interactions.api;

import com.denisjava.extended_interactions.impl.ExtendedInteractionsImpl;
import org.jetbrains.annotations.ApiStatus;

/**
 * Can be accquired in {@link EIPlugin#registerInteractions(InteractionRegistrar)}
 */
public class InteractionRegistrar {
    public void register(ExtInteraction interaction) {
        //<editor-fold desc="EI Internal Code" defaultstate="collapsed">
        ExtendedInteractionsImpl.registerInteraction(interaction);
        //</editor-fold>
    }

    //<editor-fold desc="EI Internal Code" defaultstate="collapsed">
    @ApiStatus.Internal
    public InteractionRegistrar() {}
    //</editor-fold>
}
