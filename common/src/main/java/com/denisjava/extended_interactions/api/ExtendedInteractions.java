package com.denisjava.extended_interactions.api;

import com.denisjava.extended_interactions.impl.ExtendedInteractionsImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public final class ExtendedInteractions {
    /**
     * Registers block interaction provider.<br>
     * This must be done during {@link EIPlugin#registerProviders()} or exception will be thrown!
     * @param holder Registered holder of block that will be a potential target for the provider
     * @param provider Block provider to register
     */
    public static void registerBlockProvider(Holder<? extends Block> holder, EIBlockProvider provider) {
        //<editor-fold desc="EI Internal Code" defaultstate="collapsed">
        if (holder.unwrapKey().isEmpty())
            throw new IllegalArgumentException("Provided block is not yet registered! Register it to minecraft registry before registering extended interactions to it");
        //? if >=1.21.11 {
        /*ExtendedInteractionsImpl.registerProvider(holder.unwrapKey().get().identifier(), provider);
        *///?} else
        ExtendedInteractionsImpl.registerProvider(holder.unwrapKey().get().location(), provider);
        //</editor-fold>
    }
    /**
     * Registers block interaction provider.<br>
     * This must be done during {@link EIPlugin#registerProviders()} or exception will be thrown!
     * @param block Registered block that will be a potential target for the provider
     * @param provider Block provider to register
     */
    public static void registerBlockProvider(Block block, EIBlockProvider provider) {
        //<editor-fold desc="EI Internal Code" defaultstate="collapsed">
        Optional<ResourceKey<Block>> key = BuiltInRegistries.BLOCK.getResourceKey(block);
        if (key.isEmpty())
            throw new IllegalArgumentException("Provided block is not yet registered! Register it to minecraft registry before registering extended interactions to it");
        //? if >=1.21.11 {
        /*ExtendedInteractionsImpl.registerProvider(key.get().identifier(), provider);
        *///?} else
        ExtendedInteractionsImpl.registerProvider(key.get().location(), provider);
        //</editor-fold>
    }

    /**
     * Registers entity interaction provider.<br>
     * This must be done during {@link EIPlugin#registerProviders()} or exception will be thrown!
     * @param holder Registered holder of entity type that will be a potential target for the provider
     * @param provider Entity provider to register
     */
    public static void registerEntityProvider(Holder<? extends EntityType<?>> holder, EIEntityProvider provider) {
        //<editor-fold desc="EI Internal Code" defaultstate="collapsed">
        if (holder.unwrapKey().isEmpty())
            throw new IllegalArgumentException("Provided entity type is not yet registered! Register it to minecraft registry before registering extended interactions to it");
        //? if >=1.21.11 {
        /*ExtendedInteractionsImpl.registerProvider(holder.unwrapKey().get().identifier(), provider);
        *///?} else
        ExtendedInteractionsImpl.registerProvider(holder.unwrapKey().get().location(), provider);
        //</editor-fold>
    }

    /**
     * Registers entity interaction provider.<br>
     * This must be done during {@link EIPlugin#registerProviders()} or exception will be thrown!
     * @param type Registered entity type that will be a potential target for the provider
     * @param provider Entity provider to register
     */
    public static void registerEntityProvider(EntityType<?> type, EIEntityProvider provider) {
        //<editor-fold desc="EI Internal Code" defaultstate="collapsed">
        Optional<ResourceKey<EntityType<?>>> key = BuiltInRegistries.ENTITY_TYPE.getResourceKey(type);
        if (key.isEmpty())
            throw new IllegalArgumentException("Provided entity type is not yet registered! Register it to minecraft registry before registering extended interactions to it");
        //? if >=1.21.11 {
        /*ExtendedInteractionsImpl.registerProvider(key.get().identifier(), provider);
        *///?} else
        ExtendedInteractionsImpl.registerProvider(key.get().location(), provider);
        //</editor-fold>
    }

    //<editor-fold desc="EI Internal Code" defaultstate="collapsed">
    // ♥
    //</editor-fold>
}
