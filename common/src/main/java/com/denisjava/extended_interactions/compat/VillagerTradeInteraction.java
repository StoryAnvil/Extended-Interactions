package com.denisjava.extended_interactions.compat;

import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.JavaInteraction;
import com.denisjava.extended_interactions.api.providers.EIEntityProvider;
import com.denisjava.extended_interactions.api.providers.EIResult;
import com.denisjava.extended_interactions.api.providers.EIResultCollector;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import com.denisjava.extended_interactions.mixin.EIVillager;
import com.denisjava.extended_interactions.util.ThrowableEIResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
//? if <1.21.11 {
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
//? } else {
/*import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
*///? }
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class VillagerTradeInteraction extends JavaInteraction implements EIEntityProvider {
    public VillagerTradeInteraction(ResourceLocation id, ExtInteractionIcon icon, EIPlugin declaringPlugin) {
        super(id, icon, declaringPlugin);
    }

    @Override
    public void handleEntityExecution(Player player, Level level, Entity target, String argumentId) {
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

    @Override
    public void collectForEntity(EIResultCollector collector, Level level, Player user, Entity entity) throws ThrowableEIResult {
        if (!level.isClientSide() && ((AbstractVillager) entity).getOffers().isEmpty()) {
            collector.add(EIResult.fail(this).addReason("no_trades"));
            return;
        }
        collector.add(EIResult.success(this));
    }
}
