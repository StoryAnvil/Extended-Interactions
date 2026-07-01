package com.denisjava.extended_interactions.config;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.client.EIClient;
import com.denisjava.extended_interactions.impl.EIYACLConfigFactoryImpl;
import com.denisjava.extended_interactions.impl.ExtendedInteractionsImpl;
import com.denisjava.extended_interactions.util.Lazy;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.io.File;
import java.util.function.Supplier;

import static com.denisjava.extended_interactions.EICommon.id;
import static net.minecraft.network.chat.Component.translatable;

public class EIClientConfig {
    public static ConfigClassHandler<EIClientConfig> HANDLER = ConfigClassHandler.createBuilder(EIClientConfig.class)
            .id(id("client"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(new File(EICommon.getPlatform().getConfigDir(), "extended-interactions-client.json5").toPath())
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .setJson5(true)
                    .build())
            .build();

    public static final Lazy<Option<Boolean>> PREDICT_INTERACTIONS = new Lazy<>(() -> Option.<Boolean>createBuilder()
            .name(translatable("extended_interactions.client_predict"))
            .description(OptionDescription.of(
                    translatable("extended_interactions.client_predict.help")
            ))
            .binding(true, () -> HANDLER.instance().predictInteractions, v -> HANDLER.instance().predictInteractions = v)
            .addListener(EIClientConfig::predictInteractionsListener)
            .controller(TickBoxControllerBuilder::create)
            .build());

    public static final Lazy<Option<Boolean>> ALLOW_PREDICT_USAGE = new Lazy<>(() -> Option.<Boolean>createBuilder()
            .name(translatable("extended_interactions.client_prefire"))
            .description(OptionDescription.of(
                    translatable("extended_interactions.client_prefire.help")
            ))
            .binding(true, () -> HANDLER.instance().allowPredictUsage, v -> HANDLER.instance().allowPredictUsage = v)
            .controller(TickBoxControllerBuilder::create)
            .available(HANDLER.instance().predictInteractions)
            .build());

    public static final Supplier<Option<String>> RADIAL_MENU_BIND = () -> Option.<String>createBuilder()
            .name(translatable("extended_interactions.radial_bind"))
            .description(OptionDescription.of(
                    translatable("extended_interactions.radial_bind.help"),
                    translatable("extended_interactions.bind.help")
            ))
            .controller(StringControllerBuilder::create)
            .stateManager(StateManager.createImmutable(EIClient.OPEN_RADIAL.get().getTranslatedKeyMessage().getString()))
            .available(false)
            .build();

    /**
     * Generates screen for client and server EI config.
     */
    public static Screen generateScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(translatable("extended_interactions.config"))
                .category(EIClientConfig::generateClientVisCategory)
                .category(EIClientConfig::generateClientFuncCategory)
                .build().generateScreen(parent);
    }

    public static ConfigCategory generateClientFuncCategory() {
        ConfigCategory.Builder cfg = ConfigCategory.createBuilder()
                .name(translatable("extended_interactions.client_func"))
                .tooltip(translatable("extended_interactions.client_func.tooltip"))
                .group(EIClientConfig::keyBinds)
                .group(OptionGroup.createBuilder()
                        .name(translatable("extended_interactions.client_func"))
                        .option(PREDICT_INTERACTIONS.get())
                        .option(ALLOW_PREDICT_USAGE.get())
                        .build());

        EIYACLConfigFactoryImpl factory = new EIYACLConfigFactoryImpl();
        for (EIPlugin plugin : ExtendedInteractionsImpl.getAllPlugins()) {
            factory.currentBuilder = null;
            plugin.createClientYACLConfigs(factory);
            if (factory.currentBuilder != null) {
                cfg.group(factory.currentBuilder
                        .name(translatable("extended_interactions.plugin", translatable(plugin.getUID().toLanguageKey("ei_plugin"))))
                        .build());
            }
        }

        return cfg.build();
    }

    public static ConfigCategory generateClientVisCategory() {
        return ConfigCategory.createBuilder()
                .name(translatable("extended_interactions.client_vis"))
                .tooltip(translatable("extended_interactions.client_vis.tooltip"))
                .group(EIClientConfig::keyBinds)
                .build();
    }

    private static OptionGroup keyBinds() {
        return OptionGroup.createBuilder()
                .name(translatable("extended_interactions.binds"))
                .option(RADIAL_MENU_BIND.get())
                .build();
    }

    @SerialEntry(
            comment = "When radial menu is opened, server needs to return list of actions for the radial menu.\n" +
                    "Network latency will cause this to take some time.\n" +
                    "If this is enabled, client will predict list of actions and replace them when server replies instead of waiting."
    )
    public boolean predictInteractions = true;

    @SerialEntry(
            comment = "Ignored if predictInteractions is disabled.\n" +
                    "Allows picking interaction in radial menu before server replies with actual list.\n" +
                    "This might help if network latency is significant, but may cause issues if server's interaction list does not match."
    )
    public boolean allowPredictUsage = true;

    private static void predictInteractionsListener(Option<Boolean> booleanOption, OptionEventListener.Event event) {
        ALLOW_PREDICT_USAGE.get().setAvailable(booleanOption.pendingValue());
    }
}
