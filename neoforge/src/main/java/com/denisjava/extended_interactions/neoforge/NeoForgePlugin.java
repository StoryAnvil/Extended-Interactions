package com.denisjava.extended_interactions.neoforge;

import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.impl.PluginData;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforgespi.language.ModFileScanData.AnnotationData;

import java.lang.reflect.InvocationTargetException;

public record NeoForgePlugin(ModContainer container, AnnotationData data) implements PluginData {
    @Override
    public String getModId() {
        return container.getModId();
    }

    @Override
    public EIPlugin newInstance() {
        try {
            return (EIPlugin) Class.forName(data.memberName()).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException |
                 IllegalAccessException e) {
            throw new RuntimeException("Failed to initialize extended_interactions plugin from mod: " + getModId(), e);
        }
    }
}
