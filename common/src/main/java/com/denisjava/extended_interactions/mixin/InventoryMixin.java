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

    @Inject(method = "getSelected", at = @At("HEAD"), cancellable = true)
    private void overrideSelected(CallbackInfoReturnable<ItemStack> cir) {
        Integer override = ((EIPlayer) player).ei$getSlotOverride();
        if (override == null) return;
        cir.setReturnValue(getItem(override));
    }
}
