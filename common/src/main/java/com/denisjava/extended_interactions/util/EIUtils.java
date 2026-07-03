package com.denisjava.extended_interactions.util;

import com.denisjava.extended_interactions.EICommon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;

import java.io.File;

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

    public static CompoundTag save(Entity entity) {
        //? if <1.21.11 {
        CompoundTag tag = new CompoundTag();
        entity.save(tag);
        return tag;
        //?} else {
        /*net.minecraft.world.level.storage.TagValueOutput ctx = net.minecraft.world.level.storage.TagValueOutput.createWithContext(ProblemReporter.DISCARDING, entity.level().registryAccess());
        entity.save(ctx);
        return ctx.buildResult();
        *///?}
    }
}
