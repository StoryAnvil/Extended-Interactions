package com.denisjava.extended_interactions.compat;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.*;
import com.denisjava.extended_interactions.impl.EIResultImpl;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import dev.isxander.yacl3.api.LabelOption;
import dev.isxander.yacl3.api.Option;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@EIPluginClass
public class MinecraftInteractions implements EIPlugin {
    private final DebugInteraction DEBUG1 = new DebugInteraction(EICommon.id("debug1"), new ExtInteractionIcon.ItemStackIcon(Items.DIAMOND), this);
    private final DebugInteraction DEBUG2 = new DebugInteraction(EICommon.id("debug2"), new ExtInteractionIcon.ItemStackIcon(Items.BONE), this);

    @Override
    public void registerInteractions(InteractionRegistrar registrar) {
        registrar.register(DEBUG1);
        registrar.register(DEBUG2);
    }

    @Override
    public void registerProviders() {
        ExtendedInteractions.registerBlockProvider(Blocks.GRASS_BLOCK, this::debugProvider);
        ExtendedInteractions.registerBlockProvider(Blocks.SAND, this::debugProvider);
        ExtendedInteractions.registerBlockProvider(Blocks.AMETHYST_BLOCK, this::debugProvider);

        ExtendedInteractions.registerBlockProvider(Blocks.GRASS_BLOCK, this::badDebugProvider);
        ExtendedInteractions.registerBlockProvider(Blocks.SAND, this::badDebugProvider);
        ExtendedInteractions.registerBlockProvider(Blocks.OBSIDIAN, this::badDebugProvider);
    }

    private EIResultImpl.Result debugProvider(Level level, Player player, BlockPos pos, BlockState state) {
        if (state.is(Blocks.GRASS_BLOCK)) return EIResults.success(DEBUG1);
        if (state.is(Blocks.AMETHYST_BLOCK)) return EIResults.silentFailure(DEBUG1);
        return EIResults.failure(DEBUG1, Component.literal("Bad block"));
    }

    private EIResultImpl.Result badDebugProvider(Level level, Player player, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return EIResults.success(DEBUG2);
        return EIResults.failure(DEBUG2, Component.literal("Actually no!"));
    }

    @Override
    public void createClientYACLConfigs(EIYACLConfigFactory factory) {
        factory.create().option(LabelOption.create(Component.literal("config api test")));
    }

    @Override
    public String getDeclaringModId() {
        return EICommon.MOD_ID;
    }

    @Override
    public ResourceLocation getUID() {
        return EICommon.id("minecraft");
    }
}
