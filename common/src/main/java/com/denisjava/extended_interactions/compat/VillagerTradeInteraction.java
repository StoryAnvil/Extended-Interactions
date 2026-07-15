package com.denisjava.extended_interactions.compat;

import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.JavaInteraction;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import com.denisjava.extended_interactions.mixin.EIVillager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
//? if <1.21.11 {
/*import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
*///? } else {
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
//? }
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class VillagerTradeInteraction extends JavaInteraction {
    public VillagerTradeInteraction(Identifier id, ExtInteractionIcon icon, EIPlugin declaringPlugin) {
        super(id, icon, declaringPlugin);
    }

    @Override
    public void handleEntityExecution(Player player, Level level, Entity target) {
        if (target instanceof AbstractVillager villager && !level.isClientSide()) {
            if (villager.getOffers().isEmpty()) {
                return;
            }
            if (villager instanceof Villager) ((EIVillager) villager).ei$startTrading(player);
            else {
                villager.setTradingPlayer(player);
                villager.openTradingScreen(player, target.getDisplayName(), 1);
            }
        }
    }
}
