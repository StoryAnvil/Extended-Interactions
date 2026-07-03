package com.denisjava.extended_interactions.compat;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.EIPluginClass;
import com.denisjava.extended_interactions.api.InteractionRegistrar;
import com.denisjava.extended_interactions.config.CommandAction;
import com.denisjava.extended_interactions.config.KeymappingAction;
import net.minecraft.resources.ResourceLocation;

import static com.denisjava.extended_interactions.EICommon.id;

@EIPluginClass
public class EIBuiltinPlugin implements EIPlugin {
    @Override
    public void registerInteractions(InteractionRegistrar registrar) {
        registrar.registerDataDrivenAction(id("command"), CommandAction.CODEC);
        registrar.registerDataDrivenAction(id("key"), KeymappingAction.CODEC);
    }

    @Override
    public String getDeclaringModId() {
        return EICommon.MOD_ID;
    }

    @Override
    public ResourceLocation getUID() {
        return id("builtin");
    }
}
