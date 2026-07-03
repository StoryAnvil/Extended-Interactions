package com.denisjava.extended_interactions.config;

import com.denisjava.extended_interactions.impl.MenuTarget;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public interface DataDrivenAction {
    void handle(Player player, MenuTarget target);
    ResourceLocation getId();
}
