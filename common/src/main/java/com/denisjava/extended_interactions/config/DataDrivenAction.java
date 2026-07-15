package com.denisjava.extended_interactions.config;

import com.denisjava.extended_interactions.impl.ExtendedInteractionsImpl;
import com.denisjava.extended_interactions.impl.MenuTarget;
import com.denisjava.extended_interactions.util.ThrowableEIResult;
import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public interface DataDrivenAction {
    Codec<DataDrivenAction> CODEC = Identifier.CODEC.dispatch(DataDrivenAction::getId, ExtendedInteractionsImpl::getActionCodec);

    void handle(Player player, MenuTarget target);
    default void test(DataDrivenInteraction interaction, Player player) throws ThrowableEIResult {}
    Identifier getId();
}
