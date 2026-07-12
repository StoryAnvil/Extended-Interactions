package com.denisjava.extended_interactions.api;

import com.denisjava.extended_interactions.impl.EIResultImpl;
import com.denisjava.extended_interactions.util.ThrowableEIResult;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface EIBlockProvider {
    /**
     * Tries to supply interaction for block.<br>
     * Provider should work same on logical client and logical server!
     * @param level Level block is located in.
     * @param user Player requested the interaction
     * @param pos Block's position in level
     * @param state Block's state
     * @return Provider result. See {@link EIResults#success(ExtInteraction)}, {@link EIResults#silentFailure(ExtInteraction)}, {@link EIResults#failure(ExtInteraction, Component, String)}
     */
    @NotNull EIResultImpl.Result collectForBlock(Level level, Player user, BlockPos pos, BlockState state) throws ThrowableEIResult;
}
