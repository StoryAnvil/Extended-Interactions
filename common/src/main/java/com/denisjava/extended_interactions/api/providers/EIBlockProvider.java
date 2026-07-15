package com.denisjava.extended_interactions.api.providers;

import com.denisjava.extended_interactions.util.ThrowableEIResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface EIBlockProvider {
    /**
     * Tries to supply interaction for block.<br>
     * Provider should work same on logical client and logical server!
     *
     * @param collector Object to return {@link EIResult}s to
     * @param level     Level block is located in.
     * @param user      Player requested the interaction
     * @param pos       Block's position in level
     * @param state     Block's state
     * @throws ThrowableEIResult Use {@link EIResult#throwNow()} to add {@link EIResult} to the collector immediately
     */
    void collectForBlock(EIResultCollector collector, Level level, Player user, BlockPos pos, BlockState state) throws ThrowableEIResult;
}
