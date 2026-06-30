package com.denisjava.extended_interactions.impl;

import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.EIPluginClass;

public interface PluginData {
    String getModId();
    EIPlugin newInstance();

    default EIPlugin getVerifiedInstance() {
        EIPlugin plugin = newInstance();
        if (!plugin.getDeclaringModId().equals(getModId()))
            throw new RuntimeException("Failed to create extended_interactions plugin for mod: " + getModId() + ". Mod id returned by getDeclaringModId() does not match");
        if (plugin.getClass().getAnnotation(EIPluginClass.class) == null)
            throw new RuntimeException("Failed to create extended_interactions plugin for mod: " + getModId() + ". Plugin class does not have @EIPluginClass annotation");
        return plugin;
    }
}
