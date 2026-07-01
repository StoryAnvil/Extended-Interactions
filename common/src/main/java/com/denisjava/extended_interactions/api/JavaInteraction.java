package com.denisjava.extended_interactions.api;

import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import com.denisjava.extended_interactions.impl.MenuTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class JavaInteraction extends ExtInteraction {
    public JavaInteraction(ResourceLocation id, ExtInteractionIcon icon, EIPlugin declaringPlugin) {
        super(id, icon, declaringPlugin);
    }

    @Override
    public final void handleExecution(Player player, MenuTarget target) {
        if (target instanceof MenuTarget.BlockTarget block) {
            handleBlockExecution(player, block.getLevel(), block.getPos(), block.getLevel().getBlockState(block.getPos()));
        } else if (target instanceof MenuTarget.EntityTarget entity) {
            handleEntityExecution(player, player.level(), player.level().getEntity(entity.getEntityId()));
        }
    }

    /**
     * Handles this interaction with block target.<br>
     * This done on both logical client and server.
     */
    public void handleBlockExecution(Player player, Level level, BlockPos pos, BlockState state) {}

    /**
     * Handles this interaction with entity target.<br>
     * This done on both logical client and server.
     */
    public void handleEntityExecution(Player player, Level level, Entity target) {}

    @Override
    final void _totally_not_sus_method_preventing_bad_subclasses_() {}
}
