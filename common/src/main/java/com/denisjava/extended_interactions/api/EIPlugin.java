package com.denisjava.extended_interactions.api;

import dev.isxander.yacl3.api.OptionGroup;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for all extended interactions plugins.<br>
 * Implementing classes must have {@link EIPluginClass} annotation
 */
public interface EIPlugin {
    /**
     * Called before {@link EIPlugin#registerProviders(ProviderRegistrar)} and
     * {@link EIPlugin#registerInteractions(InteractionRegistrar)}.<br>
     * Do all your preparations here.
     */
    default void init() {}

    /**
     * Register interaction providers during this method.<br>
     * Use methods from {@link ProviderRegistrar} api class.<br>
     * Providers should work both on logical client and logical server.
     */
    default void registerProviders(ProviderRegistrar registrar) {}

    /**
     * Register interactions during this method.
     */
    default void registerInteractions(InteractionRegistrar registrar) {}

    /**
     * @return mod id of mod that created this plugin. Mismatched values will cause errors!
     */
    @NotNull String getDeclaringModId();

    /**
     * @return unique identifier for this plugin. Namespace must match {@link EIPlugin#getDeclaringModId}
     */
    @NotNull ResourceLocation getUID();

    /**
     * Create client config options for this plugin.<br>
     * Created YACL {@link OptionGroup.Builder} will be added to Extended Interaction's config screen automatically.
     * You do not need to build provided builder.
     * @param factory Factory to create a {@link OptionGroup.Builder}
     */
    default void createClientYACLConfigs(EIYACLConfigFactory factory) {}

    default int loadingPriority() {
        return 1000;
    }
}
