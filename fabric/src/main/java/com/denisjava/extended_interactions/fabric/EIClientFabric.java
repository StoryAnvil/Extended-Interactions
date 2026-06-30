package com.denisjava.extended_interactions.fabric;

import com.denisjava.extended_interactions.EIPlatform;
import com.denisjava.extended_interactions.client.EIClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.function.Consumer;

public class EIClientFabric implements ClientModInitializer, EIPlatform.ClientNetworkRegistrar {
    @Override
    public void onInitializeClient() {
        EIClient.init();

        KeyBindingHelper.registerKeyBinding(EIClient.OPEN_RADIAL.get());

        ClientTickEvents.END_CLIENT_TICK.register(EIClient::clientTick);
        EIClient.registerPayloadHandlers(this);
    }

    @Override
    public <T extends CustomPacketPayload> void registerS2CHandler(CustomPacketPayload.Type<T> type, Consumer<T> listener) {
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, _) -> listener.accept(payload));
    }
}
