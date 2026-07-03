package com.denisjava.extended_interactions.neoforge;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.EIPlatform;
import com.denisjava.extended_interactions.client.EIClient;
import com.denisjava.extended_interactions.config.EIClientConfig;
import com.denisjava.extended_interactions.impl.ExtendedInteractionsImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.function.Consumer;

@Mod(value = EICommon.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = EICommon.MOD_ID, value = Dist.CLIENT)
public class EIClientNeoForge {
    public EIClientNeoForge(IEventBus bus, ModContainer container) {
        EIClient.init();
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, screen) ->
                EIClientConfig.generateScreen(screen));
        EIClient.platformKeyDebug = EIClientNeoForge::platformKeyDebug;

        ExtendedInteractionsImpl.freezeCountDown();
    }

    private static void platformKeyDebug() {

    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        EIClient.clientTick(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void registerKeymappings(RegisterKeyMappingsEvent event) {
        //? if >=1.21.11
        //event.registerCategory(EIClient.KEYMAPPING_CATEGORY);
        event.register(EIClient.OPEN_RADIAL.get());
    }

    //? if >=1.21.11 {
    /*@SubscribeEvent
    public static void registerClientPayloadHandlers(net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent event) {
        EIClient.registerPayloadHandlers(new EIPlatform.ClientNetworkRegistrar() {
            @Override
            public <T extends CustomPacketPayload> void registerS2CHandler(CustomPacketPayload.Type<T> type, Consumer<T> listener) {
                event.register(type, (t, u) -> listener.accept(t));
            }
        });
    }
    *///?}
}
