package com.denisjava.extended_interactions.compat;

import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.JavaInteraction;
import net.minecraft.resources.ResourceLocation;

public class DebugInteraction extends JavaInteraction {
    public DebugInteraction(ResourceLocation id, EIPlugin declaringPlugin) {
        super(id, null, declaringPlugin);
    }
}
