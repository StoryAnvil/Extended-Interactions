package com.denisjava.extended_interactions.mixin;

import com.denisjava.extended_interactions.util.EIPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public abstract class InventoryMixin {
    @Shadow
    @Final
    public Player player;

    @Shadow
    public abstract ItemStack getItem(int index);

    //? if <1.21.11 {
    @Inject(method = "getSelected", at = @At("HEAD"), cancellable = true)
    private void overrideSelected(CallbackInfoReturnable<ItemStack> cir) {
        Integer override = ((EIPlayer) player).ei$getSlotOverride();
        if (override == null) return;
        cir.setReturnValue(getItem(override));
    }
    //?} else {
    /*@Inject(method = "getSelectedItem", at = @At("HEAD"), cancellable = true)
    private void overrideSelected(CallbackInfoReturnable<ItemStack> cir) {
        Integer override = ((EIPlayer) player).ei$getSlotOverride();
        if (override == null) return;
        cir.setReturnValue(getItem(override));
    }

    @Inject(method = "getSelectedSlot", at = @At("HEAD"), cancellable = true)
    private void overrideSelectedSlot(CallbackInfoReturnable<Integer> cir) {
        Integer override = ((EIPlayer) player).ei$getSlotOverride();
        if (override == null) return;
        cir.setReturnValue(override);
    }
    *///?}
}
