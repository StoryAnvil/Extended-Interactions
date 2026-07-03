package com.denisjava.extended_interactions.fabric;

import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.EIPluginClass;
import com.denisjava.extended_interactions.impl.PluginData;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

public record FabricPlugin(EntrypointContainer<EIPlugin> container) implements PluginData {
    @Override
    public String getModId() {
        return container.getProvider().getMetadata().getId();
    }

    @Override
    public EIPlugin newInstance() {
        return container.getEntrypoint();
    }

    @Override
    public EIPluginClass getAnnotation() throws ClassNotFoundException {
        return Class.forName(container.getDefinition()).getAnnotation(EIPluginClass.class);
    }

    @Override
    public String getName() {
        return container.getDefinition();
    }
}
