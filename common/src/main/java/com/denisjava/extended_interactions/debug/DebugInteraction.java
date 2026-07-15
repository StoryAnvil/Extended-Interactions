package com.denisjava.extended_interactions.debug;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.JavaInteraction;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DebugInteraction extends JavaInteraction {
    public DebugInteraction(Identifier id, ExtInteractionIcon icon, EIPlugin declaringPlugin) {
        super(id, icon, declaringPlugin);
    }
    @Override
    public void handleBlockExecution(Player player, Level level, BlockPos pos, BlockState state) {
        EICommon.LOG.info("DebugInteraction {} executed on {} by {}@{}; Block target {} {}",
                id, level.isClientSide() ? "CLIENT" : "SERVER", player, level, pos, state);
    }

    @Override
    public void handleEntityExecution(Player player, Level level, Entity target) {
        EICommon.LOG.info("DebugInteraction {} executed on {} by {}@{}; Entity target {}",
                id, level.isClientSide() ? "CLIENT" : "SERVER", player, level, target);
    }
}
