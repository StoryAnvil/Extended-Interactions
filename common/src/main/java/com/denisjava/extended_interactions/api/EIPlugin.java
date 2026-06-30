package com.denisjava.extended_interactions.api;

/**
 * Interface for all extended interactions plugins.<br>
 * Implementing classes must have {@link EIPluginClass} annotation
 */
public interface EIPlugin {
    /**
     * Register interaction providers during this method.<br>
     * Use methods from {@link ExtendedInteractions} api class.<br>
     * Providers should work both on logical client and logical server.
     */
    void registerProviders();

    /**
     * Register interactions during this method.
     */
    void registerInteractions(InteractionRegistrar registrar);

    /**
     * @return mod id of mod that created this plugin. Mismatched values will cause errors!
     */
    String getDeclaringModId();
}
