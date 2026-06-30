package com.denisjava.extended_interactions;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface EIPlatform {
    boolean isModLoaded(String id);
    void sendToServer(CustomPacketPayload payload);
    void sendToClient(ServerPlayer player, CustomPacketPayload payload);

    interface NetworkRegistrar {
        <T extends CustomPacketPayload> void registerC2SPayload(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, ServerPlayer> listener);
        <T extends CustomPacketPayload> void registerS2CPayload(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec);
    }

    @FunctionalInterface
    interface ClientNetworkRegistrar {
        <T extends CustomPacketPayload> void registerS2CHandler(CustomPacketPayload.Type<T> type, Consumer<T> listener);
    }
}
