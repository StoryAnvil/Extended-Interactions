package com.denisjava.extended_interactions.client;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.impl.EIResultImpl;
import com.denisjava.extended_interactions.impl.MenuTarget;
import com.denisjava.extended_interactions.network.MenuResultPacket;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RadialMenuScreen extends Screen {
    private final MenuTarget target;
    private List<EIResultImpl.Successful> good = List.of();
    private List<EIResultImpl.Failed> bad = List.of();

    public RadialMenuScreen(MenuTarget target) {
        super(Component.empty());
        this.target = target;
    }

    @Override
    public void render(@NotNull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        int centerX = g.guiWidth() / 2;
        int centerY = g.guiHeight() / 2;
        final int radius = 48;

        for (int i = 0; i < good.size(); i++) {
            EIResultImpl.Successful result = good.get(i);
            double angle = (float) i / good.size() * Math.PI * 2;
            int x = Math.toIntExact(Math.round(Math.sin(angle) * radius));
            int y = Math.toIntExact(Math.round(-Math.cos(angle) * radius));

            MutableComponent component = result.interaction.getName();
            g.drawString(font, component, (centerX + x - font.width(component) / 2), centerY + y - 4, -1);
        }

        int y = 5;
        for (EIResultImpl.Failed result : bad) {
            MutableComponent component = result.interaction.getName();
            g.drawString(font, component, 5, y, -1);
            y += 10;
            g.drawString(font, result.error.copy().withStyle(ChatFormatting.RED), 5, y, -1);
            y += 15;
        }
    }

    @Override
    protected void init() {
        // Predict interactions on client
        Pair<List<EIResultImpl.Successful>, List<EIResultImpl.Failed>> clientSideResult = target.collectClientSide(Minecraft.getInstance().player);
        good = clientSideResult.getFirst();
        bad = clientSideResult.getSecond();

        // Request interactions from server
        EICommon.getPlatform().sendToServer(target.createRequest());
    }

    //? if >=1.21.11 {
    /*@Override
    public boolean isInGameUi() {
        return true;
    }
    *///?}

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void handleMenuResult(MenuResultPacket packet) {
        good = packet.good();
        bad = packet.bad();
    }
}
