package com.denisjava.extended_interactions.impl;

import com.denisjava.extended_interactions.network.BlockMenuRequestPacket;
import com.denisjava.extended_interactions.network.EntityMenuRequestPacket;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class MenuTarget {
    public abstract CustomPacketPayload createRequest();
    public abstract Pair<List<EIResultImpl.Successful>, List<EIResultImpl.Failed>> collectClientSide(Player player);

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
    }

    public static class EntityTarget extends MenuTarget {
        private final int entityId;

        public EntityTarget(int entityId) {
            this.entityId = entityId;
        }

        @Override
        public CustomPacketPayload createRequest() {
            return new EntityMenuRequestPacket(entityId);
        }

        @Override
        public Pair<List<EIResultImpl.Successful>, List<EIResultImpl.Failed>> collectClientSide(Player player) {
            return ExtendedInteractionsImpl.sort(ExtendedInteractionsImpl.collectForEntity(player.level(), player, player.level().getEntity(entityId)));
        }
    }
}
