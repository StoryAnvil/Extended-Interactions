package com.denisjava.extended_interactions.config;

import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.ExtInteraction;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import com.denisjava.extended_interactions.impl.MenuTarget;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class DataDrivenInteraction extends ExtInteraction {
    public DataDrivenInteraction(ResourceLocation id, ExtInteractionIcon icon, EIPlugin declaringPlugin) {
        super(id, icon, declaringPlugin);
    }

    @Override
    public void handleExecution(Player player, MenuTarget target) {

    }
}
