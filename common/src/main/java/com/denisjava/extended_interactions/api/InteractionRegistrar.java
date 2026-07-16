package com.denisjava.extended_interactions.api;

import com.denisjava.extended_interactions.config.DataDrivenAction;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * Can be accquired in {@link EIPlugin#registerInteractions(InteractionRegistrar)}
 */
public interface InteractionRegistrar {
    void register(ExtInteraction interaction);
    void registerDataDrivenAction(ResourceLocation id, MapCodec<? extends DataDrivenAction> codec);
}
