package com.denisjava.extended_interactions;

import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.EIPluginClass;
import com.denisjava.extended_interactions.api.InteractionRegistrar;
import com.denisjava.extended_interactions.config.DataDrivenActions;
import com.denisjava.extended_interactions.config.EIDataManager;
import com.denisjava.extended_interactions.impl.ExtendedInteractionsImpl;
import com.denisjava.extended_interactions.impl.PluginData;
import com.denisjava.extended_interactions.network.BlockMenuRequestPacket;
import com.denisjava.extended_interactions.network.EntityMenuRequestPacket;
import com.denisjava.extended_interactions.network.MenuResultPacket;
import com.denisjava.extended_interactions.network.RunExtInteractionPacket;
import com.denisjava.extended_interactions.util.EIUtils;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class EICommon {
    public static final String MOD_ID = "extended_interactions";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);
    private static EIPlatform platform;

    public static void init(EIPlatform platform) {
        EICommon.platform = platform;
        DataDrivenActions.register();
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
        EIUtils.checkConfigDirectory();
        List<EIPlugin> plugins = pluginCandidates.stream().map(EICommon::loadPlugin).filter(Objects::nonNull).toList();
        ExtendedInteractionsImpl.setAllPlugins(plugins);
        InteractionRegistrar registrar = new InteractionRegistrar();
        EIDataManager.load();
        for (EIPlugin plugin : plugins) {
            plugin.registerInteractions(registrar);
            plugin.registerProviders();
        }
        ExtendedInteractionsImpl.freezeRegistries();
        ExtendedInteractionsImpl.freezeCountDown();
    }

    private static EIPlugin loadPlugin(PluginData data) {
        try {
            // Check required mods
            EIPluginClass a = data.getAnnotation();
            if (a == null) throw new RuntimeException("Plugin missing com.denisjava.extended_interactions.api.EIPluginClass annotation");
            for (String modId : a.requiredMods()) {
                if (modId.equals("*dev")) {
                    if (platform.isDevEnvironment()) continue;
                    return null;
                }
                if (platform.isModLoaded(modId)) continue;
                return null;
            }

            // Create EIPlugin instance
            EIPlugin plugin = data.newInstance();
            if (!data.getModId().equals(plugin.getDeclaringModId()))
                throw new RuntimeException("Plugin's getDeclaringModId() does not return \"" + data.getModId() + "\"");
            if (!data.getModId().equals(Objects.requireNonNull(plugin.getUID()).getNamespace()))
                throw new RuntimeException("Plugin's getUID() returns ResourceLocation with incorrect namespace");

            return plugin;
        } catch (ClassNotFoundException | RuntimeException e) {
            throw new RuntimeException("Failed to load extended interaction's addon with mod id: " + data.getModId(), e);
        }
    }
}
