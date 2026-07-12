package com.denisjava.extended_interactions.impl;

import com.denisjava.extended_interactions.network.BlockMenuRequestPacket;
import com.denisjava.extended_interactions.network.EntityMenuRequestPacket;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
//? if >=1.21.11 {
/*import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionSet;
*///?}
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;

import java.util.List;

/**
 * {@link com.denisjava.extended_interactions.api.ExtInteraction}'s target.<br>
 * Separated in subclasses by target type. See {@link BlockTarget}, {@link EntityTarget}
 */
public abstract class MenuTarget {
    /**
     * Helper for {@link com.denisjava.extended_interactions.client.RadialMenuScreen}
     */
    public abstract CustomPacketPayload createRequest();

    /**
     * Helper for {@link com.denisjava.extended_interactions.client.RadialMenuScreen}.<br>
     * Returns client predicated list of interactions
     */
    public abstract Pair<List<EIResultImpl.Successful>, List<EIResultImpl.Failed>> collectClientSide(Player player);

    public abstract CommandSourceStack createStack(ServerLevel level);

    public abstract Either<BlockPos, Integer> getEither();

    public static class BlockTarget extends MenuTarget {
        private final Level level;

        private final BlockPos pos;
        public BlockTarget(Level level, BlockPos pos) {
            this.level = level;
            this.pos = pos;
        }

        @Override
        public CustomPacketPayload createRequest() {
            return new BlockMenuRequestPacket(pos);
        }

        @Override
        public Pair<List<EIResultImpl.Successful>, List<EIResultImpl.Failed>> collectClientSide(Player player) {
            return ExtendedInteractionsImpl.sort(ExtendedInteractionsImpl.collectForBlock(level, player, pos));
        }

        @Override
        public CommandSourceStack createStack(ServerLevel level) {
            return new CommandSourceStack(CommandSource.NULL, pos.getCenter(), Vec2.ZERO, level,
                    //? if <1.21.11
                    2,
                    //? if >=1.21.11
                    //LevelBasedPermissionSet.GAMEMASTER,
                    "EI BLOCK", Component.literal("EI BLOCK"), level.getServer(), null);
        }

        @Override
        public Either<BlockPos, Integer> getEither() {
            return Either.left(pos);
        }

        public BlockPos getPos() {
            return pos;
        }

        public Level getLevel() {
            return level;
        }
    }

    public static class EntityTarget extends MenuTarget {
        private final int entityId;

        public EntityTarget(int entityId) {
            this.entityId = entityId;
        }

        public int getEntityId() {
            return entityId;
        }

        @Override
        public CustomPacketPayload createRequest() {
            return new EntityMenuRequestPacket(entityId);
        }

        @Override
        public Pair<List<EIResultImpl.Successful>, List<EIResultImpl.Failed>> collectClientSide(Player player) {
            return ExtendedInteractionsImpl.sort(ExtendedInteractionsImpl.collectForEntity(player.level(), player, player.level().getEntity(entityId)));
        }

        @Override
        public CommandSourceStack createStack(ServerLevel level) {
            Entity entity = level.getEntity(entityId);
            assert entity != null;
            return new CommandSourceStack(CommandSource.NULL, entity.getPosition(1), Vec2.ZERO, level,
                    //? if <1.21.11
                    2,
                    //? if >=1.21.11
                    //LevelBasedPermissionSet.GAMEMASTER,
                    "EI ENTITY", Component.translatable("extended_interactions.cmdentity", entity.getDisplayName()),
                    level.getServer(), entity);
        }

        @Override
        public Either<BlockPos, Integer> getEither() {
            return Either.right(entityId);
        }

        public Entity get(Player player) {
            return player.level().getEntity(entityId);
        }
    }
}
