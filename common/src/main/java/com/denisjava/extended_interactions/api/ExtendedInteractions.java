package com.denisjava.extended_interactions.api;

import com.denisjava.extended_interactions.impl.ExtendedInteractionsImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public final class ExtendedInteractions {
    public static void registerBlockProvider(Holder<? extends Block> holder, EIBlockProvider provider) {
        if (holder.unwrapKey().isEmpty())
            throw new IllegalArgumentException("Provided block is not yet registered! Register it to minecraft registry before registering extended interactions to it");
        ExtendedInteractionsImpl.registerProvider(holder.unwrapKey().get().identifier(), provider);
    }
    public static void registerBlockProvider(Block block, EIBlockProvider provider) {
        Optional<ResourceKey<Block>> key = BuiltInRegistries.BLOCK.getResourceKey(block);
        if (key.isEmpty())
            throw new IllegalArgumentException("Provided block is not yet registered! Register it to minecraft registry before registering extended interactions to it");
        ExtendedInteractionsImpl.registerProvider(key.get().identifier(), provider);
    }

    public static void registerEntityProvider(Holder<? extends EntityType<?>> holder, EIEntityProvider provider) {
        if (holder.unwrapKey().isEmpty())
            throw new IllegalArgumentException("Provided entity type is not yet registered! Register it to minecraft registry before registering extended interactions to it");
        ExtendedInteractionsImpl.registerProvider(holder.unwrapKey().get().identifier(), provider);
    }
    public static void registerEntityProvider(EntityType<?> type, EIEntityProvider provider) {
        Optional<ResourceKey<EntityType<?>>> key = BuiltInRegistries.ENTITY_TYPE.getResourceKey(type);
        if (key.isEmpty())
            throw new IllegalArgumentException("Provided entity type is not yet registered! Register it to minecraft registry before registering extended interactions to it");
        ExtendedInteractionsImpl.registerProvider(key.get().identifier(), provider);
    }

    //<editor-fold desc="EI Internal Code" defaultstate="collapsed">

    //</editor-fold>
}
