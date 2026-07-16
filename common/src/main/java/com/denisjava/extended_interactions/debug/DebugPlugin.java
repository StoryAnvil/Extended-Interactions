package com.denisjava.extended_interactions.debug;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.*;
import com.denisjava.extended_interactions.api.providers.EIBlockProvider;
import com.denisjava.extended_interactions.api.providers.EIResult;
import com.denisjava.extended_interactions.api.providers.EIResultCollector;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import dev.isxander.yacl3.api.LabelOption;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import static com.denisjava.extended_interactions.EICommon.id;

@EIPluginClass(requiredMods = {"*dev"})
public class DebugPlugin implements EIPlugin {
    private final JavaInteraction[] DEBUG = new JavaInteraction[]{
            new DebugInteraction(id("debug0"), new ExtInteractionIcon.ItemStackIcon(Items.RED_DYE), this),
            new KeyDebugInteraction(id("debug1"), new ExtInteractionIcon.ItemStackIcon(Items.ORANGE_DYE), this),
            new DebugInteraction(id("debug2"), new ExtInteractionIcon.ItemStackIcon(Items.YELLOW_DYE), this),
            new DebugInteraction(id("debug3"), new ExtInteractionIcon.ItemStackIcon(Items.GREEN_DYE), this),
            new DebugInteraction(id("debug4"), new ExtInteractionIcon.ItemStackIcon(Items.LIME_DYE), this),
            new DebugInteraction(id("debug6"), new ExtInteractionIcon.ItemStackIcon(Items.LIGHT_BLUE_DYE), this),
            new DebugInteraction(id("debug7"), new ExtInteractionIcon.ItemStackIcon(Items.BLUE_DYE), this)
    };
    private final DebugInteraction DESYNC_TEST = new DebugInteraction(id("desync"), new ExtInteractionIcon.ItemStackIcon(Items.JIGSAW), this);

    @Override
    public void registerInteractions(InteractionRegistrar registrar) {
        for (JavaInteraction i : DEBUG) {
            registrar.register(i);
        }
        registrar.register(DESYNC_TEST);
    }

    @Override
    public void registerProviders(ProviderRegistrar registrar) {
        for (JavaInteraction i : DEBUG) {
            registrar.blockProvider(Blocks.DIAMOND_BLOCK, constantProvider(i));
        }
        registrar.blockProvider(Blocks.GRASS_BLOCK, constantProvider(DEBUG[0]));
        registrar.blockProvider(Blocks.GRASS_BLOCK, this::desyncTest);
        registrar.blockProvider(Blocks.GRASS_BLOCK, constantProvider(DEBUG[1]));
        registrar.blockProvider(Blocks.GRASS_BLOCK, constantProvider(DEBUG[2]));
    }

    private void desyncTest(EIResultCollector collector, Level level, Player player, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            collector.add(EIResult.fail(DESYNC_TEST).addReason(Component.literal("Actually no"), "desync-test"));
            return;
        }
        collector.add(EIResult.success(DESYNC_TEST));
    }

    private EIBlockProvider constantProvider(JavaInteraction interaction) {
        return (collector, level, user, pos, state) -> collector.add(EIResult.success(interaction));
    }

    @Override
    public String getDeclaringModId() {
        return EICommon.MOD_ID;
    }

    @Override
    public ResourceLocation getUID() {
        return id("debug");
    }

    @Override
    public void createClientYACLConfigs(EIYACLConfigFactory factory) {
        factory.create()
                .option(LabelOption.create(Component.literal("YACL Compat test")));
    }
}