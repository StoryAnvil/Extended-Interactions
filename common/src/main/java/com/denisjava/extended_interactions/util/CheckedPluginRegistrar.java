package com.denisjava.extended_interactions.util;

import com.denisjava.extended_interactions.api.EIPlugin;
import net.minecraft.resources.Identifier;

import java.util.Objects;

public class CheckedPluginRegistrar {
    private EIPlugin currentPlugin;

    public void setCurrentPlugin(EIPlugin currentPlugin) {
        this.currentPlugin = currentPlugin;
    }

    protected void assertId(Identifier identifier) {
        Objects.requireNonNull(identifier);
        if (!identifier.getNamespace().equals(currentPlugin.getDeclaringModId()))
            throw new IllegalArgumentException("Namespace of " + identifier + " does not match namespace of declaring plugin: " + currentPlugin.getDeclaringModId());
    }

    protected EIPlugin getCurrentPlugin() {
        return currentPlugin;
    }
}
