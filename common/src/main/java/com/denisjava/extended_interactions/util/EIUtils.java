package com.denisjava.extended_interactions.util;

import net.minecraft.server.MinecraftServer;

public class EIUtils {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void scheduleOnServer(MinecraftServer server, Runnable task) {
        server.submit(task);
    }
}
