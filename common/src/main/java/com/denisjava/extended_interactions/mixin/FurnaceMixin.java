package com.denisjava.extended_interactions.mixin;

import com.denisjava.extended_interactions.compat.EIContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Function;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class FurnaceMixin implements EIContainer {
    @Shadow
    protected NonNullList<ItemStack> items;

    @Shadow
    @Final
    protected static int SLOT_RESULT;

    @Shadow
    public abstract void setItem(int index, ItemStack stack);

    @Shadow
    public abstract void awardUsedRecipesAndPopExperience(ServerPlayer player);

    @Override
    public boolean ei$canTakeResult(@NotNull Player player) {
        return true;
    }

    @Override
    public boolean ei$hasResult(@NotNull Player player) {
        return !items.get(SLOT_RESULT).isEmpty();
    }

    @Override
    public void ei$takeResult(@NotNull ServerPlayer player, @NotNull Function<ItemStack, ItemStack> consumer) {
        ItemStack stack = items.get(SLOT_RESULT);
        awardUsedRecipesAndPopExperience(player);
        setItem(SLOT_RESULT, consumer.apply(stack));
        //? if <1.21.11
        stack.onCraftedBy(player.level(), player, stack.getCount() - items.get(SLOT_RESULT).getCount());
        //? if >=1.21.11
        //stack.onCraftedBy(player, stack.getCount() - items.get(SLOT_RESULT).getCount());
    }

    @Override
    public @Nullable ItemStack ei$getPreview(@NotNull Player player) {
        return items.get(SLOT_RESULT);
    }
}
