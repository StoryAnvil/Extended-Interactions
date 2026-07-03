package com.denisjava.extended_interactions.config;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.ExtInteraction;
import com.denisjava.extended_interactions.client.EIClient;
import com.denisjava.extended_interactions.impl.EIYACLConfigFactoryImpl;
import com.denisjava.extended_interactions.impl.ExtendedInteractionsImpl;
import com.denisjava.extended_interactions.util.Lazy;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.denisjava.extended_interactions.EICommon.id;
import static net.minecraft.network.chat.Component.translatable;

public class EIClientConfig {
    public static ConfigClassHandler<EIClientConfig> HANDLER = ConfigClassHandler.createBuilder(EIClientConfig.class)
            .id(id("client"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(new File(EICommon.getPlatform().getConfigDir(), "client.json5").toPath())
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    //.appendGsonBuilder(s -> s.registerTypeAdapter(new CleanMap<ExtInteractionState>().getClass(), new Clean))
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

    public static final Lazy<Option<Integer>> RADIAL_MENU_RADIUS = new Lazy<>(() -> Option.<Integer>createBuilder()
            .name(translatable("extended_interactions.radial_radius"))
            .description(OptionDescription.of(
                    translatable("extended_interactions.radial_radius.help")
            ))
            .binding(48, () -> HANDLER.instance().radialMenuRadius, v -> HANDLER.instance().radialMenuRadius = v)
            .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                    .range(10, 100).step(1).formatValue(val -> translatable("extended_interactions.pixels", String.valueOf(val))))
            .build());

    public static final Lazy<Option<Boolean>> DISPLAY_FAILED = new Lazy<>(() -> Option.<Boolean>createBuilder()
            .name(translatable("extended_interactions.display_failed"))
            .description(OptionDescription.of(
                    translatable("extended_interactions.display_failed.help")
            ))
            .binding(true, () -> HANDLER.instance().displayFailed, v -> HANDLER.instance().displayFailed = v)
            .controller(TickBoxControllerBuilder::create)
            .build());

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
            try {
                factory.currentBuilder = null;
                plugin.createClientYACLConfigs(factory);
                if (factory.currentBuilder != null) {
                    cfg.group(factory.currentBuilder
                            .name(translatable("extended_interactions.plugin", translatable(plugin.getUID().toLanguageKey("ei_plugin"))))
                            .build());
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to build YACL config group for extended interaction addon with id " + plugin.getDeclaringModId(), e);
            }
        }

        return cfg.build();
    }

    public static ConfigCategory generateClientVisCategory() {
        OptionGroup.Builder b = OptionGroup.createBuilder()
                .name(translatable("extended_interactions.eis"))
                .description(OptionDescription.of(translatable("extended_interactions.eis.help")));

        EIPlugin currentPlugin = null;
        for (ExtInteraction i : ExtendedInteractionsImpl.getAllInteractions()
                .stream().sorted(EIClientConfig::interactionSorter).toList()) {
            if (currentPlugin != i.getDeclaringPlugin()) {
                currentPlugin = i.getDeclaringPlugin();
                b.option(LabelOption.create(translatable(currentPlugin.getUID().toLanguageKey("ei_plugin"))));
            }
            b.option(interactionStateOption(i));
        }

        return ConfigCategory.createBuilder()
                .name(translatable("extended_interactions.client_vis"))
                .tooltip(translatable("extended_interactions.client_vis.tooltip"))
                .group(EIClientConfig::keyBinds)
                .group(OptionGroup.createBuilder()
                        .name(translatable("extended_interactions.radial_menu"))
                        .option(RADIAL_MENU_RADIUS.get())
                        .option(DISPLAY_FAILED.get())
                        .build())
                .group(b.build())
                .build();
    }

    private static OptionGroup keyBinds() {
        return OptionGroup.createBuilder()
                .name(translatable("extended_interactions.binds"))
                .option(RADIAL_MENU_BIND.get())
                .build();
    }

    private static Option<ExtInteractionState> interactionStateOption(final ExtInteraction interaction) {
        final String key = interaction.getId().toString();
        return Option.<ExtInteractionState>createBuilder()
                .name(interaction.getName())
                .description(OptionDescription.of(
                        translatable("extended_interactions.inter_toggle", interaction.getName()),
                        translatable("extended_interactions.inter_info", translatable(interaction.getDeclaringPlugin().getUID().toLanguageKey("ei_plugin")),
                                Component.literal(interaction.getDeclaringPlugin().getDeclaringModId()))
                ))
                .controller(opt -> EnumControllerBuilder.create(opt)
                        .enumClass(ExtInteractionState.class))
                .binding(ExtInteractionState.DEFAULT, () -> {
                    return HANDLER.instance().interactions.getOrDefault(key, ExtInteractionState.DEFAULT);
                }, s -> {
                    if (s == ExtInteractionState.DEFAULT) {
                        HANDLER.instance().interactions.remove(key);
                    } else {
                        HANDLER.instance().interactions.put(key, s);
                    }
                })
                .build();
    }

    @SerialEntry(
            comment = """
                    When radial menu is opened, server needs to return list of actions for the radial menu.
                    Network latency will cause this to take some time.
                    If this is enabled, client will predict list of actions and replace them when server replies instead of waiting."""
    )
    public boolean predictInteractions = true;

    @SerialEntry(
            comment = """
                    Ignored if predictInteractions is disabled.
                    Allows picking interaction in radial menu before server replies with actual list.
                    This might help if network latency is significant, but may cause issues if server's interaction list does not match."""
    )
    public boolean allowPredictUsage = true;

    @SerialEntry(
            comment = "Radius of radial menu in gui-scaled pixels"
    )
    public int radialMenuRadius = 48;

    @SerialEntry(
            comment = "Display failed interactions"
    )
    public boolean displayFailed = true;

    @SerialEntry(
            comment = """
                    Client-side disabled interactions.
                    Interaction id <-> DEFAULT/HIDE/HIDE_FAILURES
                    DEFAULT - interaction is enabled
                    HIDE - interaction is fully hidden
                    HIDE_FAILURES - interactions is only visible if it is available to use
                    """
    )
    public Map<String, ExtInteractionState> interactions = new HashMap<>();

    private static void predictInteractionsListener(Option<Boolean> booleanOption, OptionEventListener.Event event) {
        ALLOW_PREDICT_USAGE.get().setAvailable(booleanOption.pendingValue());
    }

    private static int interactionSorter(ExtInteraction i1, ExtInteraction i2) {
        return i1.getDeclaringPlugin().getUID().compareTo(i2.getDeclaringPlugin().getUID());
    }
}
