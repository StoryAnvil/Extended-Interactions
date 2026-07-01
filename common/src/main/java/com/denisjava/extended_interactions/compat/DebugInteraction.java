package com.denisjava.extended_interactions.compat;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.JavaInteraction;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class DebugInteraction extends JavaInteraction {
    public DebugInteraction(ResourceLocation id, ExtInteractionIcon icon, EIPlugin declaringPlugin) {
        super(id, icon, declaringPlugin);
    }

    @Override
    public void handleBlockExecution(Player player, Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            EICommon.LOG.info("Executing debug on client");
        } else {
            EICommon.LOG.info("Executing debug on server");
            Vec3 c = pos.getCenter();
            level.playSound(null, c.x, c.y, c.z, SoundEvents.ALLAY_ITEM_GIVEN, SoundSource.NEUTRAL);
        }
    }
}
