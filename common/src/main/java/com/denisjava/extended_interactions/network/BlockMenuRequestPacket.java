package com.denisjava.extended_interactions.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

import static com.denisjava.extended_interactions.EICommon.id;

public record BlockMenuRequestPacket(BlockPos pos) implements CustomPacketPayload {
    public static final Type<BlockMenuRequestPacket> TYPE = new Type<>(id("request_block"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockMenuRequestPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, BlockMenuRequestPacket::pos,
            BlockMenuRequestPacket::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
