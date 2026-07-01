package com.denisjava.extended_interactions.fabric;

import com.denisjava.extended_interactions.config.EIClientConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return EIClientConfig::generateScreen;
    }
}
