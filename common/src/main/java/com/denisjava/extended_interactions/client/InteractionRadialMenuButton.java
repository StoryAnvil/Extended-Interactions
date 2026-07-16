package com.denisjava.extended_interactions.client;

import com.denisjava.extended_interactions.api.ExtInteraction;
import com.denisjava.extended_interactions.api.InteractionArgument;
import org.jetbrains.annotations.Nullable;

public interface InteractionRadialMenuButton {
    ExtInteraction getInteraction();
    @Nullable InteractionArgument getArgument();
}
