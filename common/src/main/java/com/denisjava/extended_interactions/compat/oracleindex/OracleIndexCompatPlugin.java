//? if oracle_index {
package com.denisjava.extended_interactions.compat.oracleindex;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.EIPluginClass;
import com.denisjava.extended_interactions.api.InteractionRegistrar;
import com.denisjava.extended_interactions.api.ProviderRegistrar;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import static com.denisjava.extended_interactions.EICommon.id;

@EIPluginClass(requiredMods = "oracle_index")
public class OracleIndexCompatPlugin implements EIPlugin {
    @Override
    public void registerInteractions(@NotNull InteractionRegistrar registrar) {
        registrar.register(WIKI_LOOKUP);
    }

    @Override
    public void registerProviders(@NotNull ProviderRegistrar registrar) {
        registrar.universalBlockProvider(WIKI_LOOKUP);
    }

    @Override
    public @NotNull String getDeclaringModId() {
        return EICommon.MOD_ID;
    }

    @Override
    public @NotNull ResourceLocation getUID() {
        return id("oracle_index");
    }

    // == WIKI LOOKUP ==
    private final OracleIndexLookupInteraction WIKI_LOOKUP = new OracleIndexLookupInteraction(id("oracle_index/lookup"), new ExtInteractionIcon.ItemStackIcon(Items.WRITABLE_BOOK), this);
}
//? }