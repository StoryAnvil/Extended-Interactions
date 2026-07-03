package com.denisjava.extended_interactions.config;

import com.denisjava.extended_interactions.impl.ExtendedInteractionsImpl;
import com.denisjava.extended_interactions.impl.MenuTarget;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public interface DataDrivenAction {
    Codec<DataDrivenAction> CODEC = ResourceLocation.CODEC.dispatch(DataDrivenAction::getId, ExtendedInteractionsImpl::getActionCodec);

    void handle(Player player, MenuTarget target);
    ResourceLocation getId();
}
