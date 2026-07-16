package com.denisjava.extended_interactions.impl;

import com.denisjava.extended_interactions.api.ExtInteraction;
import com.denisjava.extended_interactions.api.InteractionRegistrar;
import com.denisjava.extended_interactions.config.DataDrivenAction;
import com.denisjava.extended_interactions.util.CheckedPluginRegistrar;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

public class InteractionRegistrarImpl extends CheckedPluginRegistrar implements InteractionRegistrar {
    @Override
    public void register(ExtInteraction interaction) {
        assertId(interaction.getId());
        if (interaction.getDeclaringPlugin() != getCurrentPlugin())
            throw new IllegalArgumentException(getCurrentPlugin().getClass().getSimpleName() + " attempted to register " + interaction + " that belongs to "
                    + interaction.getDeclaringPlugin().getClass().getSimpleName());
        ExtendedInteractionsImpl.registerInteraction(interaction);
    }

    @Override
    public void registerDataDrivenAction(ResourceLocation id, MapCodec<? extends DataDrivenAction> codec) {
        assertId(id);
        ExtendedInteractionsImpl.registerAction(id, codec);
    }
}
