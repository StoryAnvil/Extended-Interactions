package com.denisjava.extended_interactions.network;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static com.denisjava.extended_interactions.EICommon.id;

public record RunExtInteractionPacket(Either<BlockPos, Integer> target, ResourceLocation interaction) implements CustomPacketPayload {
    public static final Type<RunExtInteractionPacket> TYPE = new Type<>(id("run"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RunExtInteractionPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.either(BlockPos.STREAM_CODEC, ByteBufCodecs.INT), RunExtInteractionPacket::target,
            ResourceLocation.STREAM_CODEC, RunExtInteractionPacket::interaction,
            RunExtInteractionPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
