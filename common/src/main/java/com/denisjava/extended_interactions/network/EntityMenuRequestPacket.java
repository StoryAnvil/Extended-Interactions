package com.denisjava.extended_interactions.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import static com.denisjava.extended_interactions.EICommon.id;

public record EntityMenuRequestPacket(int entityId) implements CustomPacketPayload {
    public static final Type<EntityMenuRequestPacket> TYPE = new Type<>(id("request_entity"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EntityMenuRequestPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, EntityMenuRequestPacket::entityId,
            EntityMenuRequestPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
