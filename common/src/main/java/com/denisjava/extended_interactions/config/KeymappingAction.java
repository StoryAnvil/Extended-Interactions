package com.denisjava.extended_interactions.config;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.client.EIClient;
import com.denisjava.extended_interactions.impl.MenuTarget;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record KeymappingAction(String keymapping) implements DataDrivenAction {
    public static final MapCodec<KeymappingAction> CODEC =
            Codec.STRING.fieldOf("key").xmap(KeymappingAction::new, KeymappingAction::keymapping);

    @Override
    public void handle(Player player, MenuTarget target) {
        if (player.level().isClientSide()) EIClient.pressKey(keymapping);
    }

    @Override
    public ResourceLocation getId() {
        return EICommon.id("key");
    }
}
