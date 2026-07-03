package com.denisjava.extended_interactions.mixin;

import com.denisjava.extended_interactions.util.EIPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements EIPlayer {
    @Unique
    private Integer ei$slotOverride = null;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void ei$overrideMainHandSlot() {
        ei$slotOverride = null;
    }

    @Override
    public void ei$overrideMainHandSlot(int slotId) {
        if (slotId == -1) {
            ei$slotOverride = null;
            return;
        }
        ei$slotOverride = slotId;
    }

    @Override
    public @Nullable Integer ei$getSlotOverride() {
        return ei$slotOverride;
    }
}
