package com.denisjava.extended_interactions.compat;

import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.EIResults;
import com.denisjava.extended_interactions.api.ExtInteraction;
import com.denisjava.extended_interactions.api.JavaInteraction;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import com.denisjava.extended_interactions.util.EIUtils;
import com.denisjava.extended_interactions.util.ThrowableEIResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class LeashInteraction extends JavaInteraction implements ExtInteraction.EntityProvider {
    public LeashInteraction(ResourceLocation id, ExtInteractionIcon icon, EIPlugin declaringPlugin) {
        super(id, icon, declaringPlugin);
    }

    @Override
    public void handleEntityExecution(Player player, Level level, Entity target) {
        if (!level.isClientSide() && target instanceof Leashable leashable && leashable.canBeLeashed() && target.isAlive()
                && !(target instanceof LivingEntity le && le.isBaby())) {
            int slot = EIUtils.findItem(player, Items.LEAD);
            if (slot == -1) return;
            player.getInventory().getItem(slot).consume(1, player);
            leashable.setLeashedTo(player, true);
            //? if >=1.21.11
            //target.playSound(SoundEvents.LEAD_TIED);
            //? if <1.21.11
            target.playSound(SoundEvents.LEASH_KNOT_PLACE);
        }
    }

    @Override
    public void providerCheck(Player player, Entity target) throws ThrowableEIResult {
        if (EIUtils.findItem(player, Items.LEAD) == -1)
            throw new ThrowableEIResult(EIResults.failure(this, "no_item"));
    }
}
