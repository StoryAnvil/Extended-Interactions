package com.denisjava.extended_interactions.impl;

import com.denisjava.extended_interactions.api.providers.EIBlockProvider;
import com.denisjava.extended_interactions.api.providers.EIEntityProvider;
import com.denisjava.extended_interactions.api.ProviderRegistrar;
import com.denisjava.extended_interactions.compat.EIContainer;
import com.denisjava.extended_interactions.util.EIProviderRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;

import java.util.Optional;

public final class ProviderRegistrarImpl implements ProviderRegistrar {
    @Override
    public void blockProvider(Holder<? extends Block> holder, EIBlockProvider provider) {
        if (holder.unwrapKey().isEmpty())
            throw new IllegalArgumentException("Provided block is not yet registered! Register it to minecraft registry before registering extended interactions to it");
        //? if >=1.21.11 {
        /*var key = holder.unwrapKey().get().identifier();
        *///?} else
        var key = holder.unwrapKey().get().location();
        ExtendedInteractionsImpl.BLOCK_PROVIDERS.register(key, provider);
    }

    @Override
    public void blockProvider(Block block, EIBlockProvider provider) {
        Optional<ResourceKey<Block>> rkey = BuiltInRegistries.BLOCK.getResourceKey(block);
        if (rkey.isEmpty())
            throw new IllegalArgumentException("Provided block is not yet registered! Register it to minecraft registry before registering extended interactions to it");
        //? if >=1.21.11 {
        /*var key = rkey.get().identifier();
        *///?} else
        var key = rkey.get().location();
        ExtendedInteractionsImpl.BLOCK_PROVIDERS.register(key, provider);
    }

    @Override
    public void blockTagProvider(TagKey<Block> tag, EIBlockProvider provider) {
        ExtendedInteractionsImpl.BLOCK_PROVIDERS.registerToTag(tag.location(), provider);
    }

    @Override
    public void universalBlockProvider(EIBlockProvider provider) {
        ExtendedInteractionsImpl.BLOCK_PROVIDERS.register(EIProviderRegistry.ALL, provider);
    }

    @Override
    public void entityProvider(Holder<? extends EntityType<?>> holder, EIEntityProvider provider) {
        if (holder.unwrapKey().isEmpty())
            throw new IllegalArgumentException("Provided entity type is not yet registered! Register it to minecraft registry before registering extended interactions to it");
        //? if >=1.21.11 {
        /*var key = holder.unwrapKey().get().identifier();
        *///?} else
        var key = holder.unwrapKey().get().location();
        ExtendedInteractionsImpl.ENTITY_PROVIDERS.register(key, provider);
    }

    @Override
    public void entityProvider(EntityType<?> type, EIEntityProvider provider) {
        Optional<ResourceKey<EntityType<?>>> rkey = BuiltInRegistries.ENTITY_TYPE.getResourceKey(type);
        if (rkey.isEmpty())
            throw new IllegalArgumentException("Provided entity type is not yet registered! Register it to minecraft registry before registering extended interactions to it");
        //? if >=1.21.11 {
        /*var key = rkey.get().identifier();
        *///?} else
        var key = rkey.get().location();
        ExtendedInteractionsImpl.ENTITY_PROVIDERS.register(key, provider);
    }

    @Override
    public void entityTagProvider(TagKey<EntityType<?>> tag, EIEntityProvider provider) {
        ExtendedInteractionsImpl.ENTITY_PROVIDERS.registerToTag(tag.location(), provider);
    }

    @Override
    public void universalEntityProvider(EIEntityProvider provider) {
        ExtendedInteractionsImpl.ENTITY_PROVIDERS.register(EIProviderRegistry.ALL, provider);
    }
}
