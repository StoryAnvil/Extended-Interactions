package com.denisjava.extended_interactions.debug;

import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.JavaInteraction;
import com.denisjava.extended_interactions.client.EIClient;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class KeyDebugInteraction extends JavaInteraction {
    public KeyDebugInteraction(ResourceLocation id, ExtInteractionIcon icon, EIPlugin declaringPlugin) {
        super(id, icon, declaringPlugin);
    }

    @Override
    public void handleBlockExecution(Player player, Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) EIClient.debugKeys();
    }
}
