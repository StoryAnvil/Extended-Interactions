package com.denisjava.extended_interactions.neoforge;

import com.denisjava.extended_interactions.EICommands;
import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.EIPlatform;
import com.denisjava.extended_interactions.api.EIPluginClass;
import com.denisjava.extended_interactions.impl.PluginData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforgespi.language.IModFileInfo;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.function.BiConsumer;

@Mod(EICommon.MOD_ID)
@EventBusSubscriber(modid = EICommon.MOD_ID)
public class EINeoForge implements EIPlatform {
    public EINeoForge(IEventBus bus, ModContainer container) {
        EICommon.init(this);
    }

    @Override
    public boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        ClientPacketDistributor.sendToServer(payload);
    }

    @Override
    public void sendToClient(ServerPlayer player, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        EICommands.registerCommands(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent setupEvent) {
        ArrayList<PluginData> plugins = new ArrayList<>();
        for (ModContainer container : ModList.get().getSortedMods()) {
            IModFileInfo file = container.getModInfo().getOwningFile();
            if (file == null) continue;
            file.getFile().getScanResult()
                    .getAnnotatedBy(EIPluginClass.class, ElementType.TYPE)
                    .map(d -> (PluginData) new NeoForgePlugin(container, d))
                    .forEach(plugins::add);
        }
        EICommon.registerPlugins(plugins);
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        EICommon.registerPayloads(new NetworkRegistrar() {
            @Override
            public <T extends CustomPacketPayload> void registerC2SPayload(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, ServerPlayer> listener) {
                registrar.playToServer(type, codec, (t, context) -> listener.accept(t, (ServerPlayer) context.player()));
            }

            @Override
            public <T extends CustomPacketPayload> void registerS2CPayload(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
                registrar.playToClient(type, codec);
            }
        });
    }
}
