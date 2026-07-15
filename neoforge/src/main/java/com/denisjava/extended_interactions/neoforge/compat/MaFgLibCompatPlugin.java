package com.denisjava.extended_interactions.neoforge.compat;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.EIPluginClass;
import com.denisjava.extended_interactions.api.InteractionRegistrar;
import net.minecraft.resources.Identifier;

import static com.denisjava.extended_interactions.EICommon.id;

@EIPluginClass(requiredMods = "mafglib")
public class MaFgLibCompatPlugin implements EIPlugin {
    @Override
    public void registerInteractions(InteractionRegistrar registrar) {
        registrar.registerDataDrivenAction(id("mafglib"), MaFgLibAction.CODEC);
    }

    @Override
    public String getDeclaringModId() {
        return EICommon.MOD_ID;
    }

    @Override
    public Identifier getUID() {
        return id("mafglib_compat");
    }
}
