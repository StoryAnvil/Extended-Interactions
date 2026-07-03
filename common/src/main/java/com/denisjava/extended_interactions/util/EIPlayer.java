package com.denisjava.extended_interactions.util;

import org.jetbrains.annotations.Nullable;

public interface EIPlayer {
    void ei$overrideMainHandSlot(int slotId);
    void ei$overrideMainHandSlot();

    @Nullable Integer ei$getSlotOverride();
}
