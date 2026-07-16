package com.denisjava.extended_interactions.config;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.ExtInteraction;
import com.denisjava.extended_interactions.api.providers.FailedResult;
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
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            .binding(false, () -> HANDLER.instance().allowPredictUsage, v -> HANDLER.instance().allowPredictUsage = v)
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

    public static final Lazy<Option<Boolean>> REPORT_NO_ITEM = new Lazy<>(() -> Option.<Boolean>createBuilder()
            .name(translatable("extended_interactions.report_no_item"))
            .description(OptionDescription.of(
                    translatable("extended_interactions.report_no_item.help")
            ))
            .binding(true, () -> HANDLER.instance().reportNoItem, v -> HANDLER.instance().reportNoItem = v)
            .controller(TickBoxControllerBuilder::create)
            .build());

    public static final Lazy<ListOption<ConfiguredSubmenu>> SUBMENUES = new Lazy<>(() -> ListOption.<ConfiguredSubmenu>createBuilder()
            .name(translatable("extended_interactions.submenues"))
            .description(OptionDescription.of(
                    translatable("extended_interactions.submenues.help")
            ))
            .binding(List.of(), () -> HANDLER.instance().submenus, v -> HANDLER.instance().submenus = v)
            .controller(ConfiguredSubmenu::controller)
            .initial(ConfiguredSubmenu::new)
            .build());

    public static final Lazy<Option<Boolean>> ADVANCED_INFO = new Lazy<>(() -> Option.<Boolean>createBuilder()
            .name(translatable("extended_interactions.advanced"))
            .binding(false, () -> HANDLER.instance().displayAdvancedInfo, v -> HANDLER.instance().displayAdvancedInfo = v)
            .controller(TickBoxControllerBuilder::create)
            .build());

    /**
     * Generates screen for client and server EI config.
     */
    public static Screen generateScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(translatable("extended_interactions.config"))
                .category(EIClientConfig::generateClientVisCategory)
                .category(EIClientConfig::generateClientCategoryCategory)
                .category(EIClientConfig::generateClientFuncCategory)
                .save(HANDLER::save)
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

        cfg.group(OptionGroup.createBuilder()
                        .name(translatable("extended_interactions.dangerous_cfg"))
                        .option(ADVANCED_INFO.get())
                .build());

        return cfg.build();
    }

    public static ConfigCategory generateClientVisCategory() {
        OptionGroup.Builder hideInteractions = OptionGroup.createBuilder()
                .name(translatable("extended_interactions.eis"))
                .description(OptionDescription.of(translatable("extended_interactions.eis.help")));

        EIPlugin currentPlugin = null;
        for (ExtInteraction i : ExtendedInteractionsImpl.getAllInteractions()
                .stream().sorted(EIClientConfig::interactionSorter).toList()) {
            if (currentPlugin != i.getDeclaringPlugin()) {
                currentPlugin = i.getDeclaringPlugin();
                hideInteractions.option(LabelOption.create(translatable(currentPlugin.getUID().toLanguageKey("ei_plugin"))));
            }
            hideInteractions.option(interactionStateOption(i));
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
                .group(OptionGroup.createBuilder()
                        .name(translatable("extended_interactions.report"))
                        .option(REPORT_NO_ITEM.get())
                        .build())
                .group(hideInteractions.build())
                .build();
    }

    public static ConfigCategory generateClientCategoryCategory() {
        OptionGroup.Builder interactionGroups = OptionGroup.createBuilder()
                .name(translatable("extended_interactions.submenu_binds"));

        EIPlugin currentPlugin = null;
        for (ExtInteraction interaction : ExtendedInteractionsImpl.getAllInteractions()
                .stream().sorted(EIClientConfig::interactionSorter).toList()) {
            if (currentPlugin != interaction.getDeclaringPlugin()) {
                currentPlugin = interaction.getDeclaringPlugin();
                interactionGroups.option(LabelOption.create(translatable(currentPlugin.getUID().toLanguageKey("ei_plugin"))));
            }
            final String id = interaction.getId().toString();
            interactionGroups.option(Option.<String>createBuilder()
                            .name(interaction.getName())
                            .description(OptionDescription.of(
                                    translatable("extended_interactions.inter_info", translatable(interaction.getDeclaringPlugin().getUID().toLanguageKey("ei_plugin")),
                                            Component.literal(interaction.getDeclaringPlugin().getDeclaringModId())),
                                    Component.literal(interaction.getId().toString()).withStyle(ChatFormatting.GRAY)
                            ))
                            .controller(ConfiguredSubmenu::selectorController)
                            .binding("", () -> HANDLER.instance().submenuBinds.getOrDefault(id, ""), v -> {
                                if (v.isEmpty()) HANDLER.instance().submenuBinds.remove(id);
                                else HANDLER.instance().submenuBinds.put(id, v);
                            })
                    .build());
        }

        return ConfigCategory.createBuilder()
                .name(translatable("extended_interactions.client_category"))
                .tooltip(translatable("extended_interactions.client_category.tooltip"))
                .group(SUBMENUES.get())
                .group(interactionGroups.build())
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
                                Component.literal(interaction.getDeclaringPlugin().getDeclaringModId())),
                        Component.literal(interaction.getId().toString()).withStyle(ChatFormatting.GRAY)
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
    public boolean allowPredictUsage = false;

    @SerialEntry(
            comment = "Radius of radial menu in gui-scaled pixels"
    )
    public int radialMenuRadius = 48;

    @SerialEntry(
            comment = "Display failed interactions"
    )
    public boolean displayFailed = true;

    @SerialEntry(
            comment = "Display interactions that failed due to missing items"
    )
    public boolean reportNoItem = true;

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

    @SerialEntry(
            comment = "Do not collapse interactions to submenu if all of actions belong to same submenu"
    ) // TODO: Add to config UI
    public boolean dontCollapseToSingleCategory = true;

    @SerialEntry(
            comment = "Do not collapse interactions to submenu if this submenu only has one available interaction"
    ) // TODO: Add to config UI
    public boolean expandSingleItemCategories = true;

    @SerialEntry(
            comment = "If enabled, some advanced information will be displayed. For developer use"
    )
    public boolean displayAdvancedInfo = false;

    @SerialEntry(
            comment = "Use ingame config ui"
    )
    public List<ConfiguredSubmenu> submenus = new ArrayList<>();

    @SerialEntry(
            comment = "Use ingame config ui"
    )
    public Map<String, String> submenuBinds = new HashMap<>();

    public ConfiguredSubmenu getSubmenuByName(String name) {
        for (ConfiguredSubmenu submenu : submenus) {
            if (submenu.name().equals(name)) return submenu;
        }
        return null;
    }
    public ConfiguredSubmenu getPendingSubmenuByName(String name) {
        for (ConfiguredSubmenu submenu : SUBMENUES.get().pendingValue()) {
            if (submenu.name().equals(name)) return submenu;
        }
        return null;
    }

    private static void predictInteractionsListener(Option<Boolean> booleanOption, OptionEventListener.Event event) {
        ALLOW_PREDICT_USAGE.get().setAvailable(booleanOption.pendingValue());
    }

    private static int interactionSorter(ExtInteraction i1, ExtInteraction i2) {
        return i1.getDeclaringPlugin().getUID().compareTo(i2.getDeclaringPlugin().getUID());
    }

    public boolean failureFilter(FailedResult failed) {
        return reportNoItem || !"no_item".equals(failed.getErrorCode());
    }
}
