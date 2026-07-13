package com.denisjava.extended_interactions.client;

import com.denisjava.extended_interactions.impl.RadialMenuButton;

public interface ClientRadialMenuButton extends RadialMenuButton {
    default boolean isClientSide() {
        return true;
    }

    void executeClientSide(RadialMenuScreen screen);
}
