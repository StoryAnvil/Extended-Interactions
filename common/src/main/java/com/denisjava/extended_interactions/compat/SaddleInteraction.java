package com.denisjava.extended_interactions.compat;

import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.JavaInteraction;
import com.denisjava.extended_interactions.api.providers.EIEntityProvider;
import com.denisjava.extended_interactions.api.providers.EIResult;
import com.denisjava.extended_interactions.api.providers.EIResultCollector;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import com.denisjava.extended_interactions.util.EIPlayer;
import com.denisjava.extended_interactions.util.EIUtils;
import com.denisjava.extended_interactions.util.ThrowableEIResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SaddleInteraction extends JavaInteraction implements EIEntityProvider {
    public SaddleInteraction(ResourceLocation id, ExtInteractionIcon icon, EIPlugin declaringPlugin) {
        super(id, icon, declaringPlugin);
    }

    @Override
    public void handleEntityExecution(Player player, Level level, Entity target, String argumentId) {
        //? if <1.21.11 {
        if (target instanceof net.minecraft.world.entity.Saddleable saddleable) {
            if (!saddleable.isSaddled() && saddleable.isSaddleable() && !level.isClientSide()) {
                int slot = EIUtils.findItem(player, Items.SADDLE);
                if (slot == -1) return;
                try {
                    ((EIPlayer) player).ei$overrideMainHandSlot(slot);
                    ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
                    saddleable.equipSaddle(stack.split(1), net.minecraft.sounds.SoundSource.NEUTRAL);
                    target.level().gameEvent(target, net.minecraft.world.level.gameevent.GameEvent.EQUIP, target.position());
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

    @Override
    public void collectForEntity(EIResultCollector collector, Level level, Player user, Entity entity) throws ThrowableEIResult {
        //? if <1.21.11 {
        if (!(entity instanceof net.minecraft.world.entity.Saddleable saddleable)) {
            collector.add(EIResult.fail(this));
            return;
        }
        if (saddleable.isSaddled()) {
            collector.add(EIResult.fail(this));
            return;
        }
        if (!saddleable.isSaddleable()) {
            collector.add(EIResult.fail(this).addReason("nah"));
            return;
        }
        //? } else {
        /*if (!(entity instanceof Mob mob)) {
            collector.add(EIResult.fail(this));
            return;
        }
        if (mob.isSaddled()) {
            collector.add(EIResult.fail(this));
            return;
        }
        if (!mob.isEquippableInSlot(new ItemStack(Items.SADDLE), EquipmentSlot.SADDLE)) {
            collector.add(EIResult.fail(this).addReason("nah"));
            return;
        }
        *///? }
        if (EIUtils.findItem(user, Items.SADDLE) == -1) {
            collector.add(EIResult.fail(this).addReason("no_item"));
            return;
        }
        collector.add(EIResult.success(this));
    }
}
