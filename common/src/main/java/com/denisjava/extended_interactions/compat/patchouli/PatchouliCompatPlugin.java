//? if patchouli {
package com.denisjava.extended_interactions.compat.patchouli;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.EIPluginClass;
import com.denisjava.extended_interactions.api.InteractionRegistrar;
import com.denisjava.extended_interactions.api.ProviderRegistrar;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import net.minecraft.resources.ResourceLocation;
import vazkii.patchouli.common.item.PatchouliItems;

import static com.denisjava.extended_interactions.EICommon.id;

@EIPluginClass(requiredMods = "patchouli")
public class PatchouliCompatPlugin implements EIPlugin {
    @Override
    public void registerInteractions(InteractionRegistrar registrar) {
        registrar.register(BOOK_LOOKUP);
    }

    @Override
    public void registerProviders(ProviderRegistrar registrar) {
        registrar.universalBlockProvider(BOOK_LOOKUP);
    }

    @Override
    public String getDeclaringModId() {
        return EICommon.MOD_ID;
    }

    @Override
    public ResourceLocation getUID() {
        return id("patchouli");
    }

    // == BOOK LOOKUP ==
    private final PatchouliLookupInteraction BOOK_LOOKUP = new PatchouliLookupInteraction(id("patchouli/lookup"),
            new ExtInteractionIcon.ItemStackIcon(PatchouliItems.BOOK), this);
}
//? }