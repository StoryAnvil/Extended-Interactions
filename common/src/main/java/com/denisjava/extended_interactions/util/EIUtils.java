package com.denisjava.extended_interactions.util;

import com.denisjava.extended_interactions.EICommon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.File;
import java.util.function.Predicate;

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
}
