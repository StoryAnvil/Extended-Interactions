package com.denisjava.extended_interactions.compat;

import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.JavaInteraction;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import com.denisjava.extended_interactions.util.EIPlayer;
import com.denisjava.extended_interactions.util.EIUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
//? <1.21.11
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class SaddleInteraction extends JavaInteraction {
    public SaddleInteraction(ResourceLocation id, ExtInteractionIcon icon, EIPlugin declaringPlugin) {
        super(id, icon, declaringPlugin);
    }

    @Override
    public void handleEntityExecution(Player player, Level level, Entity target) {
        ((net.minecraft.world.entity.animal.pig.Pig) target).isSaddled();

        //? if <1.21.11 {
        if (target instanceof Saddleable saddleable) {
            if (!saddleable.isSaddled() && saddleable.isSaddleable() && !level.isClientSide()) {
                int slot = EIUtils.findItem(player, Items.SADDLE);
                if (slot == -1) return;
                try {
                    ((EIPlayer) player).ei$overrideMainHandSlot(slot);
                    ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
                    saddleable.equipSaddle(stack.split(1), SoundSource.NEUTRAL);
                    target.level().gameEvent(target, GameEvent.EQUIP, target.position());
                } finally {
                    ((EIPlayer) player).ei$overrideMainHandSlot();
                }
            }
        }
        //? } else {
        /*//noinspection ConstantValue
        if (target instanceof Mob mob && !mob.isSaddled()) {
            int slot = EIUtils.findItem(player, Items.SADDLE);
            if (slot == -1) return;
            try {
                ((EIPlayer) player).ei$overrideMainHandSlot(slot);
                ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (mob.isEquippableInSlot(stack, EquipmentSlot.SADDLE)) {
                    stack.interactLivingEntity(player, mob, InteractionHand.MAIN_HAND);
                }
            } finally {
                ((EIPlayer) player).ei$overrideMainHandSlot();
            }
        }
        *///? }
    }
}
