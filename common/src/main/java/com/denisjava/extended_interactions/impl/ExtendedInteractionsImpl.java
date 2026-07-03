package com.denisjava.extended_interactions.impl;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.EIBlockProvider;
import com.denisjava.extended_interactions.api.EIEntityProvider;
import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.ExtInteraction;
import com.denisjava.extended_interactions.config.DataDrivenAction;
import com.denisjava.extended_interactions.network.BlockMenuRequestPacket;
import com.denisjava.extended_interactions.network.EntityMenuRequestPacket;
import com.denisjava.extended_interactions.network.MenuResultPacket;
import com.denisjava.extended_interactions.network.RunExtInteractionPacket;
import com.denisjava.extended_interactions.util.EIProviderRegistry;
import com.denisjava.extended_interactions.util.EIResultCollector;
import com.denisjava.extended_interactions.util.EIUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.stream.Stream;

public class ExtendedInteractionsImpl {
    public static final EIProviderRegistry<EIBlockProvider> BLOCK_PROVIDERS = new EIProviderRegistry<>();
    public static final EIProviderRegistry<EIEntityProvider> ENTITY_PROVIDERS = new EIProviderRegistry<>();
    private static final HashMap<ResourceLocation, ExtInteraction> INTERACTIONS = new HashMap<>();
    private static final HashMap<ResourceLocation, MapCodec<? extends DataDrivenAction>> DD_ACTIONS = new HashMap<>();
    private static List<EIPlugin> ALL_PLUGINS = List.of();
    private static int frozen = 2;

    public static final StreamCodec<ByteBuf, ExtInteraction> INTERACTION_STREAM_CODEC = ResourceLocation.STREAM_CODEC.map(identifier -> {
        ExtInteraction interaction = INTERACTIONS.get(identifier);
        if (interaction == null) throw new RuntimeException("Failed to decode interaction with id [" + identifier + "]. Maybe it does not exist on client?");
        return interaction;
    }, ExtInteraction::getId);
    public static final StreamCodec<ByteBuf, EIResultImpl.Successful> SUCCESSFUL_STREAM_CODEC = StreamCodec.composite(
            INTERACTION_STREAM_CODEC, EIResultImpl.Successful::getInteraction,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), EIResultImpl.Successful::getOptionalIconOverride,
            EIResultImpl.Successful::new
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, EIResultImpl.Failed> FAILED_STREAM_CODEC = StreamCodec.composite(
            INTERACTION_STREAM_CODEC, EIResultImpl.Failed::getInteraction,
            ComponentSerialization.STREAM_CODEC, EIResultImpl.Failed::getError,
            EIResultImpl.Failed::new
    );

    public static void registerInteraction(ExtInteraction interaction) {
        if (frozen <= 0) throw new IllegalStateException("Extended interactions registry is already frozen! You are registering interactions too late.");

        if (INTERACTIONS.containsKey(interaction.getId()))
            throw new IllegalStateException("ExtInteraction with id " + interaction.getId() + " is already registered!");

        INTERACTIONS.put(interaction.getId(), interaction);
    }

    public static void registerAction(ResourceLocation id, MapCodec<? extends DataDrivenAction> codec) {
        if (frozen <= 0) throw new IllegalStateException("Extended interactions registry is already frozen! You are registering actions too late.");

        if (DD_ACTIONS.containsKey(id))
            throw new IllegalStateException("DataDrivenAction with id " + id + " is already registered!");

        DD_ACTIONS.put(id, codec);
    }

    public static MapCodec<? extends DataDrivenAction> getActionCodec(ResourceLocation id) {
        return DD_ACTIONS.get(id);
    }

    public static void freezeRegistries() {
        BLOCK_PROVIDERS.freeze();
        ENTITY_PROVIDERS.freeze();
    }

    public static void freezeCountDown() {
        frozen--;
    }

    public static List<EIResultImpl.Result> collectForBlock(Level level, Player player, BlockPos pos) {
        EIResultCollector collector = new EIResultCollector();
        BlockState state = level.getBlockState(pos);

        Optional<ResourceKey<Block>> optionalKey = BuiltInRegistries.BLOCK.getResourceKey(state.getBlock());
        if (optionalKey.isEmpty()) throw new AssertionError("BlockState with unregistered block? Nah. I'm not dealing with this");
        //? if >=1.21.11 {
        /*ResourceLocation key = optionalKey.get().identifier();
        @SuppressWarnings("OptionalGetWithoutIsPresent") // Block must be registered for previous code to work. No need to recheck
        Stream<TagKey<Block>> tags = BuiltInRegistries.BLOCK.get(optionalKey.get()).get().tags();
        *///?} else {
        ResourceLocation key = optionalKey.get().location();
        Stream<TagKey<Block>> tags = BuiltInRegistries.BLOCK.getHolder(optionalKey.get().location()).get().tags();
        //? }

        for (Iterable<EIBlockProvider> providers : BLOCK_PROVIDERS.listAll(key, tags).toList()) {
            for (EIBlockProvider provider : providers) {
                collector.offer(provider.collectForBlock(level, player, pos, state));
            }
        }

        return collector;
    }

    public static List<EIResultImpl.Result> collectForEntity(Level level, Player player, Entity entity) {
        if (entity == null) return List.of();
        EIResultCollector collector = new EIResultCollector();

        Optional<ResourceKey<EntityType<?>>> optionalKey = BuiltInRegistries.ENTITY_TYPE.getResourceKey(entity.getType());
        if (optionalKey.isEmpty()) throw new AssertionError("BlockState with unregistered entity type? Nah. I'm not dealing with this");
        //? if >=1.21.11 {
        /*ResourceLocation key = optionalKey.get().identifier();
        @SuppressWarnings("OptionalGetWithoutIsPresent") // Block must be registered for previous code to work. No need to recheck
        Stream<TagKey<EntityType<?>>> tags = BuiltInRegistries.ENTITY_TYPE.get(optionalKey.get()).get().tags();
        *///?} else {
        ResourceLocation key = optionalKey.get().location();
        Stream<TagKey<EntityType<?>>> tags = BuiltInRegistries.ENTITY_TYPE.getHolder(optionalKey.get().location()).get().tags();
        //? }

        for (Iterable<EIEntityProvider> providers : ENTITY_PROVIDERS.listAll(key, tags).toList()) {
            for (EIEntityProvider provider : providers) {
                collector.offer(provider.collectForEntity(level, player, entity));
            }
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

    /**
     * Handle player's request to perform interaction.
     */
    public static void handleRun(RunExtInteractionPacket packet, ServerPlayer player) {
        ExtInteraction interaction = INTERACTIONS.get(packet.interaction());
        if (interaction == null) return;

        MenuTarget target;
        if (packet.target().left().isPresent()) {
            BlockPos pos = packet.target().left().get();
            target = new MenuTarget.BlockTarget(player.level(), pos);
        } else if (packet.target().right().isPresent()) {
            int entityId = packet.target().right().get();
            target = new MenuTarget.EntityTarget(entityId);
        } else throw new AssertionError("Bad interaction target!");

        EIUtils.scheduleOnServer(player.level().getServer(), () -> {
            interaction.handleExecution(player, target);
        });
    }

    public static Pair<List<EIResultImpl.Successful>, List<EIResultImpl.Failed>> sort(List<EIResultImpl.Result> results) {
        return new Pair<>(
                results.stream().filter(result -> result instanceof EIResultImpl.Successful).map(result -> (EIResultImpl.Successful) result).toList(),
                results.stream().filter(result -> result instanceof EIResultImpl.Failed).map(result -> (EIResultImpl.Failed) result).toList()
        );
    }

    public static Iterable<ExtInteraction> getAllInteractions() {
        return INTERACTIONS.values();
    }

    public static void setAllPlugins(List<EIPlugin> allPlugins) {
        ALL_PLUGINS = allPlugins;
    }

    public static List<EIPlugin> getAllPlugins() {
        return ALL_PLUGINS;
    }
}
