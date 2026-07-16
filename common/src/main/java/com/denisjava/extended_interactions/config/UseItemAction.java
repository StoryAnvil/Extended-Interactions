package com.denisjava.extended_interactions.config;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.providers.EIResult;
import com.denisjava.extended_interactions.impl.MenuTarget;
import com.denisjava.extended_interactions.util.EIPlayer;
import com.denisjava.extended_interactions.util.EIUtils;
import com.denisjava.extended_interactions.util.ThrowableEIResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
//? if <1.21.11
import net.minecraft.advancements.critereon.ItemPredicate;
//? if >=1.21.11
//import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public record UseItemAction(ItemPredicate item) implements DataDrivenAction {
    public static final MapCodec<UseItemAction> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ItemPredicate.CODEC.fieldOf("item").forGetter(UseItemAction::item)
    ).apply(inst, UseItemAction::new));

    @Override
    public void handle(Player player, MenuTarget target) {
        int slot = EIUtils.findItem(player, item);
        if (slot == -1) return;
        try {
            ((EIPlayer) player).ei$overrideMainHandSlot(slot);
            if (target instanceof MenuTarget.EntityTarget e) {
                player.interactOn(e.get(player), InteractionHand.MAIN_HAND);
            } else if (target instanceof MenuTarget.BlockTarget b) {
                BlockState state = b.getLevel().getBlockState(b.getPos());
                state.useItemOn(player.getItemInHand(InteractionHand.MAIN_HAND), player.level(), player,
                        InteractionHand.MAIN_HAND, new BlockHitResult(
                                b.getPos().getCenter(), Direction.UP, b.getPos(), true));
            }
        } finally {
            ((EIPlayer) player).ei$overrideMainHandSlot();
        }
    }

    @Override
    public void test(DataDrivenInteraction interaction, Player player) throws ThrowableEIResult {
        int slot = EIUtils.findItem(player, item);
        if (slot == -1) EIResult.fail(interaction).addReason("no_item").throwNow();
    }

    @Override
    public ResourceLocation getId() {
        return EICommon.id("use_item");
    }
}
