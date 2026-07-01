package com.denisjava.extended_interactions.client;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.EIPlatform;
import com.denisjava.extended_interactions.config.EIClientConfig;
import com.denisjava.extended_interactions.impl.MenuTarget;
import com.denisjava.extended_interactions.network.MenuResultPacket;
import com.denisjava.extended_interactions.util.EIUtils;
import com.denisjava.extended_interactions.util.Lazy;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.lwjgl.glfw.GLFW;

import static com.denisjava.extended_interactions.EICommon.id;

public class EIClient {
    //? if >=1.21.11 {
    /*public static final KeyMapping.Category KEYMAPPING_CATEGORY = KeyMapping.Category.register(id("main"));
    public static final Lazy<KeyMapping> OPEN_RADIAL = new Lazy<>(() -> new KeyMapping(
            "key.extended_interactions.open_radial", InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_TAB, KEYMAPPING_CATEGORY
    ));
     *///?} else {
    public static final Lazy<KeyMapping> OPEN_RADIAL = new Lazy<>(() -> new KeyMapping(
            "key.extended_interactions.open_radial", InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_TAB, "key.category.extended_interactions.main"
    ));
    //?}

    public static void init() {
        EIUtils.checkConfigDirectory();
        EIClientConfig.HANDLER.load();
    }

    public static void registerPayloadHandlers(EIPlatform.ClientNetworkRegistrar registrar) {
        registrar.registerS2CHandler(MenuResultPacket.TYPE, EIClient::handleMenuResult);
    }

    /**
     * Handles server's update of radial menu actions.<br>
     * This is used to replace client prediction of radial menu
     */
    private static void handleMenuResult(MenuResultPacket packet) {
        if (Minecraft.getInstance().screen instanceof RadialMenuScreen screen) {
            screen.handleMenuResult(packet);
        }
    }

    public static void clientTick(Minecraft minecraft) {
        while (OPEN_RADIAL.get().consumeClick()) {
            MenuTarget target = null;

            if (minecraft.hitResult.getType() == HitResult.Type.BLOCK && minecraft.hitResult instanceof BlockHitResult hit) {
                target = new MenuTarget.BlockTarget(minecraft.level, hit.getBlockPos());
            } else if (minecraft.hitResult.getType() == HitResult.Type.ENTITY && minecraft.hitResult instanceof EntityHitResult hit) {
                target = new MenuTarget.EntityTarget(hit.getEntity().getId());
            }

            if (target != null) {
                minecraft.setScreen(new RadialMenuScreen(target));
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void scheduleClient(Runnable runnable) {
        Minecraft.getInstance().submit(runnable);
    }
}
