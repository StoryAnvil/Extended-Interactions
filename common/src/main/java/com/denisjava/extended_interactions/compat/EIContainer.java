package com.denisjava.extended_interactions.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Interface used by {@link TakeResultInteraction} to get result from containers.<br>
 * This interface should be implemented by {@link net.minecraft.world.level.block.entity.BlockEntity}s.
 */
public interface EIContainer {
    /**
     * Checks if player allowed to get result from this container.<br>
     * This method does not take into account if container has any result available.
     * Only checks for permission.
     */
    boolean ei$canTakeResult(Player player);

    /**
     * Checks if this container has result available.<br>
     * This method is called only if {@link EIContainer#ei$canTakeResult(Player)} returned true for this player.
     * @return True if result is not {@link ItemStack#isEmpty()}
     */
    boolean ei$hasResult(Player player);

    /**
     * Takes result from this container and gives it to provided {@link Function}.<br>
     * {@link Function}'s return value is leftover of items that did not fit in player's inventory.
     * This stack should be added back to container's result.<br>
     * Always called on server.
     */
    void ei$takeResult(ServerPlayer player, Function<ItemStack, ItemStack> consumer);

    /**
     * Returns preview of result or null if no preview is available.<br>
     * This is only called if {@link EIContainer#ei$canTakeResult(Player)} and {@link EIContainer#ei$hasResult(Player)} returned true.
     */
    @Nullable ItemStack ei$getPreview(Player player);

    /**
     * Version {@link EIContainer} of blocks that don't have block entity attached to them, but want to provide items
     */
    interface BlockContainer {
        /**
         * @see EIContainer#ei$canTakeResult(Player)
         */
        boolean ei$canTakeResult(Player player, Level level, BlockPos pos, BlockState state);

        /**
         * @see EIContainer#ei$hasResult(Player)
         */
        boolean ei$hasResult(Player player, Level level, BlockPos pos, BlockState state);

        /**
         * @see EIContainer#ei$takeResult(ServerPlayer, Function)
         */
        void ei$takeResult(ServerPlayer player, Function<ItemStack, ItemStack> consumer, Level level, BlockPos pos, BlockState state);

        /**
         * @see EIContainer#ei$getPreview(Player)
         */
        @Nullable ItemStack ei$getPreview(Player player, Level level, BlockPos pos, BlockState state);
    }
}
