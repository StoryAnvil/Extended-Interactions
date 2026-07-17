package com.denisjava.extended_interactions.api;

import com.denisjava.extended_interactions.api.providers.EIBlockProvider;
import com.denisjava.extended_interactions.api.providers.EIEntityProvider;
import com.denisjava.extended_interactions.compat.EIContainer;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;

public interface ProviderRegistrar {
    /**
     * Registers block interaction provider.<br>
     * This must be done during {@link EIPlugin#registerProviders(ProviderRegistrar)} or exception will be thrown!
     * @param holder Registered holder of block that will be a potential target for the provider
     * @param provider Block provider to register
     */
    void blockProvider(Holder<? extends Block> holder, EIBlockProvider provider);

    /**
     * Registers block interaction provider.<br>
     * This must be done during {@link EIPlugin#registerProviders(ProviderRegistrar)} or exception will be thrown!
     * @param block Registered block that will be a potential target for the provider
     * @param provider Block provider to register
     */
    void blockProvider(Block block, EIBlockProvider provider);

    /**
     * Registers block interaction provider to block tag.<br>
     * This must be done during {@link EIPlugin#registerProviders(ProviderRegistrar)} or exception will be thrown!
     * @param tag Tag to register provider for
     * @param provider Block provider to register
     */
    void blockTagProvider(TagKey<Block> tag, EIBlockProvider provider);

    /**
     * Registers block interaction provider to all blocks.<br>
     * This must be done during {@link EIPlugin#registerProviders(ProviderRegistrar)} or exception will be thrown!
     * @param provider Block provider to register
     */
    void universalBlockProvider(EIBlockProvider provider);

    /**
     * Registers entity interaction provider.<br>
     * This must be done during {@link EIPlugin#registerProviders(ProviderRegistrar)} or exception will be thrown!
     * @param holder Registered holder of entity type that will be a potential target for the provider
     * @param provider Entity provider to register
     */
    void entityProvider(Holder<? extends EntityType<?>> holder, EIEntityProvider provider);

    /**
     * Registers entity interaction provider.<br>
     * This must be done during {@link EIPlugin#registerProviders(ProviderRegistrar)} or exception will be thrown!
     * @param type Registered entity type that will be a potential target for the provider
     * @param provider Entity provider to register
     */
    void entityProvider(EntityType<?> type, EIEntityProvider provider);

    /**
     * Registers entity interaction provider to entity type tag.<br>
     * This must be done during {@link EIPlugin#registerProviders(ProviderRegistrar)} or exception will be thrown!
     * @param tag Tag to register provider for
     * @param provider Entity provider to register
     */
    void entityTagProvider(TagKey<EntityType<?>> tag, EIEntityProvider provider);

    /**
     * Registers entity interaction provider to all entity types.<br>
     * This must be done during {@link EIPlugin#registerProviders(ProviderRegistrar)} or exception will be thrown!
     * @param provider Entity provider to register
     */
    void universalEntityProvider(EIEntityProvider provider);

    //<editor-fold desc="EI Internal Code" defaultstate="collapsed">
    // ♥
    //</editor-fold>
}
