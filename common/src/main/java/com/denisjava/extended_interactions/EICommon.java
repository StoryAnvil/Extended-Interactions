package com.denisjava.extended_interactions;

import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.InteractionRegistrar;
import com.denisjava.extended_interactions.impl.ExtendedInteractionsImpl;
import com.denisjava.extended_interactions.impl.PluginData;
import com.denisjava.extended_interactions.network.BlockMenuRequestPacket;
import com.denisjava.extended_interactions.network.EntityMenuRequestPacket;
import com.denisjava.extended_interactions.network.MenuResultPacket;
import com.denisjava.extended_interactions.network.RunExtInteractionPacket;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EICommon {
    public static final String MOD_ID = "extended_interactions";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);
    private static EIPlatform platform;

    public static void init(EIPlatform platform) {
        EICommon.platform = platform;
    }

    public static EIPlatform getPlatform() {
        return platform;
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void registerPayloads(EIPlatform.NetworkRegistrar registrar) {
        registrar.registerC2SPayload(BlockMenuRequestPacket.TYPE, BlockMenuRequestPacket.STREAM_CODEC, ExtendedInteractionsImpl::handleRequest);
        registrar.registerC2SPayload(EntityMenuRequestPacket.TYPE, EntityMenuRequestPacket.STREAM_CODEC, ExtendedInteractionsImpl::handleRequest);
        registrar.registerC2SPayload(RunExtInteractionPacket.TYPE, RunExtInteractionPacket.STREAM_CODEC, ExtendedInteractionsImpl::handleRun);

        registrar.registerS2CPayload(MenuResultPacket.TYPE, MenuResultPacket.STREAM_CODEC);
    }

    public static void registerPlugins(List<PluginData> pluginCandidates) {
        List<EIPlugin> plugins = pluginCandidates.stream().map(PluginData::getVerifiedInstance).toList();
        ExtendedInteractionsImpl.setAllPlugins(plugins);
        InteractionRegistrar registrar = new InteractionRegistrar();
        for (EIPlugin plugin : plugins) {
            plugin.registerInteractions(registrar);
            plugin.registerProviders();
        }
        ExtendedInteractionsImpl.freezeRegistries();
    }
}
