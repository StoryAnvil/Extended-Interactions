package com.denisjava.extended_interactions.compat;

import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.JavaInteraction;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import com.denisjava.extended_interactions.util.EIUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class UnleashInteraction extends JavaInteraction {
    public UnleashInteraction(ResourceLocation id, ExtInteractionIcon icon, EIPlugin declaringPlugin) {
        super(id, icon, declaringPlugin);
    }

    @Override
    public void handleEntityExecution(Player player, Level level, Entity target) {
        if (target instanceof Leashable leashable) {
            boolean hasSpace = player.getInventory().getFreeSlot() != -1 || EIUtils.findItem(player, Items.LEAD) != -1;
            //? if <1.21.11
            leashable.dropLeash(true, !hasSpace && !player.hasInfiniteMaterials());
            //? if >=1.21.11 {
            /*if (!hasSpace && !player.hasInfiniteMaterials()) leashable.dropLeash();
            else leashable.removeLeash();
            *///? }
            if (hasSpace && !player.hasInfiniteMaterials()) {
                player.getInventory().add(new ItemStack(Items.LEAD));
            }

            //? if >=1.21.11
            //target.playSound(SoundEvents.LEAD_UNTIED);
            //? if <1.21.11
            target.playSound(SoundEvents.LEASH_KNOT_BREAK);
        }
    }
}
