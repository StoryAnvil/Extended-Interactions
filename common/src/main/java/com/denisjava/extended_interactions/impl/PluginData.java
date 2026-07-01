package com.denisjava.extended_interactions.impl;

import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.EIPluginClass;

/**
 * Platform-implemented interface.<br>
 * Instances store information about single extended interactions plugin
 */
public interface PluginData {
    String getModId();
    EIPlugin newInstance();
    EIPluginClass getAnnotation() throws ClassNotFoundException;
}
