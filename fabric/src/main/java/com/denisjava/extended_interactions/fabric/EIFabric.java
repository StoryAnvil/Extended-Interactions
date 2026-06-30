package com.denisjava.extended_interactions.fabric;

import com.denisjava.extended_interactions.EICommands;
import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.EIPlatform;
import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.impl.PluginData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;

public class EIFabric implements ModInitializer, EIPlatform, EIPlatform.NetworkRegistrar {
    @Override
    public void onInitialize() {
        EICommon.init(this);
        CommandRegistrationCallback.EVENT.register(EICommands::registerCommands);
        EICommon.registerPlugins(FabricLoader.getInstance().getEntrypointContainers("extended_interactions", EIPlugin.class)
                .stream()
                .map(container -> (PluginData) new FabricPlugin(container.getProvider(), container.getEntrypoint()))
                .toList()
        );

        EICommon.registerPayloads(this);
    }

    @Override
    public boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }

    @Override
    public void sendToClient(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    @Override
    public <T extends CustomPacketPayload> void registerC2SPayload(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, ServerPlayer> listener) {
        PayloadTypeRegistry.playC2S().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> listener.accept(payload, context.player()));
    }

    @Override
    public <T extends CustomPacketPayload> void registerS2CPayload(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(type, codec);
    }
}
