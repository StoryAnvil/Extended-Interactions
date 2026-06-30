package com.denisjava.extended_interactions.impl;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.EIBlockProvider;
import com.denisjava.extended_interactions.api.EIEntityProvider;
import com.denisjava.extended_interactions.api.ExtInteraction;
import com.denisjava.extended_interactions.network.BlockMenuRequestPacket;
import com.denisjava.extended_interactions.network.EntityMenuRequestPacket;
import com.denisjava.extended_interactions.network.MenuResultPacket;
import com.denisjava.extended_interactions.util.EIProviderRegistry;
import com.denisjava.extended_interactions.util.EIResultCollector;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ExtendedInteractionsImpl {
    private static final EIProviderRegistry<Identifier, EIBlockProvider> BLOCK_PROVIDERS = new EIProviderRegistry<>();
    private static final EIProviderRegistry<Identifier, EIEntityProvider> ENTITY_PROVIDERS = new EIProviderRegistry<>();
    private static final HashMap<Identifier, ExtInteraction> INTERACTIONS = new HashMap<>();
    private static boolean frozen = false;

    public static final StreamCodec<ByteBuf, ExtInteraction> INTERACTION_STREAM_CODEC = Identifier.STREAM_CODEC.map(identifier -> {
        ExtInteraction interaction = INTERACTIONS.get(identifier);
        if (interaction == null) throw new RuntimeException("Failed to decode interaction with id [" + identifier + "]. Maybe it does not exist on client?");
        return interaction;
    }, ExtInteraction::getId);
    public static final StreamCodec<ByteBuf, EIResultImpl.Successful> SUCCESSFUL_STREAM_CODEC = INTERACTION_STREAM_CODEC.map(EIResultImpl.Successful::new, EIResultImpl.Successful::getInteraction);
    public static final StreamCodec<RegistryFriendlyByteBuf, EIResultImpl.Failed> FAILED_STREAM_CODEC = StreamCodec.composite(
            INTERACTION_STREAM_CODEC, EIResultImpl.Failed::getInteraction,
            ComponentSerialization.STREAM_CODEC, EIResultImpl.Failed::getError,
            EIResultImpl.Failed::new
    );

    public static void registerProvider(Identifier subject, EIBlockProvider provider) {
        BLOCK_PROVIDERS.register(subject, provider);
    }
    public static void registerProvider(Identifier subject, EIEntityProvider provider) {
        ENTITY_PROVIDERS.register(subject, provider);
    }
    public static void registerInteraction(ExtInteraction interaction) {
        if (frozen) throw new IllegalStateException("Extended interactions registry is already frozen! You are registering interactions too late.");

        if (INTERACTIONS.containsKey(interaction.getId()))
            throw new IllegalStateException("ExtInteraction with id " + interaction.getId() + " is already registered!");

        INTERACTIONS.put(interaction.getId(), interaction);
    }

    public static void freezeRegistries() {
        BLOCK_PROVIDERS.freeze();
        ENTITY_PROVIDERS.freeze();
        frozen = true;
    }
    public static List<EIResultImpl.Result> collectForBlock(Level level, Player player, BlockPos pos) {
        EIResultCollector collector = new EIResultCollector();
        BlockState state = level.getBlockState(pos);

        Optional<ResourceKey<Block>> optionalKey = BuiltInRegistries.BLOCK.getResourceKey(state.getBlock());
        if (optionalKey.isEmpty()) throw new AssertionError("BlockState with unregistered block? Nah. I'm dealing with this");
        Identifier key = optionalKey.get().identifier();

        for (EIBlockProvider provider : BLOCK_PROVIDERS.listAll(key)) {
            collector.offer(provider.collectForBlock(level, player, pos, state));
        }

        return collector;
    }

    public static List<EIResultImpl.Result> collectForEntity(Level level, Player player, Entity entity) {
        if (entity == null) return List.of();
        EIResultCollector collector = new EIResultCollector();

        Optional<ResourceKey<EntityType<?>>> optionalKey = BuiltInRegistries.ENTITY_TYPE.getResourceKey(entity.getType());
        if (optionalKey.isEmpty()) throw new AssertionError("BlockState with unregistered entity type? Nah. I'm dealing with this");
        Identifier key = optionalKey.get().identifier();

        for (EIEntityProvider provider : ENTITY_PROVIDERS.listAll(key)) {
            collector.offer(provider.collectForEntity(level, player, entity));
        }

        return collector;
    }

    public static void handleRequest(BlockMenuRequestPacket packet, ServerPlayer player) {
        List<EIResultImpl.Result> results = collectForBlock(player.level(), player, packet.pos());
        EICommon.getPlatform().sendToClient(player, MenuResultPacket.create(results));
    }

    public static void handleRequest(EntityMenuRequestPacket packet, ServerPlayer player) {
        List<EIResultImpl.Result> results = collectForEntity(player.level(), player, player.level().getEntity(packet.entityId()));
        EICommon.getPlatform().sendToClient(player, MenuResultPacket.create(results));
    }

    public static Pair<List<EIResultImpl.Successful>, List<EIResultImpl.Failed>> sort(List<EIResultImpl.Result> results) {
        return new Pair<>(
                results.stream().filter(result -> result instanceof EIResultImpl.Successful).map(result -> (EIResultImpl.Successful) result).toList(),
                results.stream().filter(result -> result instanceof EIResultImpl.Failed).map(result -> (EIResultImpl.Failed) result).toList()
        );
    }
}
