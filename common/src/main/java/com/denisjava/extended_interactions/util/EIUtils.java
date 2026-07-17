package com.denisjava.extended_interactions.util;

import com.denisjava.extended_interactions.EICommon;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class EIUtils {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void scheduleOnServer(MinecraftServer server, Runnable task) {
        server.submit(task);
    }

    public static void checkConfigDirectory() {
        File dir = EICommon.getPlatform().getConfigDir();
        if (!dir.exists()) {
            if (!dir.mkdirs())
                EICommon.LOG.error("Failed to create extended-interaction's config directory!");
        }
    }

    @SuppressWarnings("ConstantValue")
    public static int findItem(Player player, Predicate<ItemStack> predicate) {
        Container inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack != null && predicate.test(stack)) {
                return i;
            }
        }
        return -1;
    }

    public static int findItem(Player player, Item item) {
        return findItem(player, stack -> stack.is(item));
    }

    public static CompoundTag save(Entity entity) {
        //? if <1.21.11 {
        CompoundTag tag = new CompoundTag();
        entity.save(tag);
        return tag;
        //?} else {
        /*net.minecraft.world.level.storage.TagValueOutput ctx = net.minecraft.world.level.storage.TagValueOutput.createWithContext(
                net.minecraft.util.ProblemReporter.DISCARDING, entity.level().registryAccess());
        entity.save(ctx);
        return ctx.buildResult();
        *///?}
    }

    /**
     * Sorts provided stream same way as original list is sorted.<br>
     * For example if original list is [Z, A, B, D, C, Q] and unordered stream is [D, Q, Z, B]
     * result will be [Z, B, D, Q].
     * @param mapper Function that maps values to unique ids. For example {@link Object#hashCode()}
     * @param original Original list of values. Unordered stream will be sorted like this list
     * @param unordered Stream to sort
     * @return Sorted stream
     */
    public static <R, INDEX> Stream<R> preservedSort(Function<R, INDEX> mapper, List<R> original, Stream<R> unordered) {
        List<INDEX> indices = original.stream().map(mapper).toList();
        return unordered.sorted(Comparator.comparingInt(o -> indices.indexOf(mapper.apply(o))));
    }

    public static TagKey<Block> blocks(String name) {
        return TagKey.create(Registries.BLOCK, EICommon.id(name));
    }

    public static TagKey<EntityType<?>> entities(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, EICommon.id(name));
    }

    public static <T> String encodeToString(T object, Codec<T> codec) {
        return codec.encodeStart(JsonOps.COMPRESSED, object).getOrThrow().toString();
    }
    public static <T> @Nullable T decodeFromString(String object, Codec<T> codec) {
        try {
            DataResult<Pair<T, JsonElement>> dataResult = codec.decode(JsonOps.COMPRESSED, JsonParser.parseString(object));
            if (dataResult.isError()) return null;
            return dataResult.getOrThrow().getFirst();
        } catch (com.google.gson.JsonSyntaxException e) {
            return null;
        }
    }
}
