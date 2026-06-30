package com.denisjava.extended_interactions.fabric;

import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.impl.PluginData;
import net.fabricmc.loader.api.ModContainer;

public record FabricPlugin(ModContainer provider, EIPlugin entrypoint) implements PluginData {
    @Override
    public String getModId() {
        return provider.getMetadata().getId();
    }

    @Override
    public EIPlugin newInstance() {
        return entrypoint;
    }
}
