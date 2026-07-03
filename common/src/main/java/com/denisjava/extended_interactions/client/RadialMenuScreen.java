package com.denisjava.extended_interactions.client;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.ExtInteraction;
import com.denisjava.extended_interactions.config.EIClientConfig;
import com.denisjava.extended_interactions.config.ExtInteractionState;
import com.denisjava.extended_interactions.impl.EIResultImpl;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import com.denisjava.extended_interactions.impl.MenuTarget;
import com.denisjava.extended_interactions.network.MenuResultPacket;
import com.denisjava.extended_interactions.network.RunExtInteractionPacket;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
//? if >=1.21.11
//import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class RadialMenuScreen extends Screen {
    private static final ResourceLocation SLOT_SPRITE = ResourceLocation.withDefaultNamespace("gamemode_switcher/slot");
    private static final ResourceLocation SELECTION_SPRITE = ResourceLocation.withDefaultNamespace("gamemode_switcher/selection");
    private final MenuTarget target;
    private List<EIResultImpl.Successful> successfulResults = List.of();
    private List<EIResultImpl.Failed> failedResults = List.of();
    private ActionData[] successful = new ActionData[0];
    private boolean serverReplied = false;

    private int selectedInteraction = -1;

    public RadialMenuScreen(MenuTarget target) {
        super(Component.empty());
        this.target = target;
    }

    @Override
    public void render(@NotNull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        int centerX = g.guiWidth() / 2;
        int centerY = g.guiHeight() / 2;

        for (ActionData s : successful) {
            int i1 = centerY + s.y;
            int i2 = centerX + s.x;
            //? if >=1.21.11
            //g.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_SPRITE, i2 - 13, i1 - 13, 26, 26);
            //? if <1.21.11
            g.blitSprite(SLOT_SPRITE, i2 - 13, i1 - 13, 26, 26);
            s.icon.render16x16(g, font, i2 - 8, i1 - 8);
        }
        if (selectedInteraction != -1) {
            ActionData s = successful[selectedInteraction];
            //? if >=1.21.11
            //g.blitSprite(RenderPipelines.GUI_TEXTURED, SELECTION_SPRITE, centerX + s.x - 13, centerY + s.y - 13, 26, 26);
            //? if <1.21.11
            g.blitSprite(SELECTION_SPRITE, centerX + s.x - 13, centerY + s.y - 13, 26, 26);
            g.drawString(font, s.name, centerX - font.width(s.name) / 2, centerY - 4, -1);
        }

        int y = 5;
        for (EIResultImpl.Failed result : failedResults) {
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
        if (EIClientConfig.HANDLER.instance().predictInteractions) {
            Pair<List<EIResultImpl.Successful>, List<EIResultImpl.Failed>> clientSideResult = target.collectClientSide(Minecraft.getInstance().player);
            successfulResults = clientSideResult.getFirst();
            failedResults = clientSideResult.getSecond();
        }
        bake();

        // Request interactions from server
        EICommon.getPlatform().sendToServer(target.createRequest());
    }

    private void bake() {
        final int radius = EIClientConfig.HANDLER.instance().radialMenuRadius;
        Map<String, ExtInteractionState> stateMap = EIClientConfig.HANDLER.instance().interactions;

        successfulResults = successfulResults.stream().filter(s ->
                stateMap.get(s.interaction.getId().toString()) != ExtInteractionState.HIDE).toList();
        failedResults = failedResults.stream().filter(s ->
                stateMap.getOrDefault(s.interaction.getId().toString(), ExtInteractionState.DEFAULT) == ExtInteractionState.DEFAULT).toList();

        successful = new ActionData[successfulResults.size()];
        for (int i = 0; i < successful.length; i++) {
            EIResultImpl.Successful result = successfulResults.get(i);
            double angle = (float) i / successful.length * Math.PI * 2;

            successful[i] = new ActionData(
                    result.iconOverride == null ? result.interaction.getIcon() : result.interaction.getIcon(result.iconOverride),
                    Math.toIntExact(Math.round(Math.sin(angle) * radius)),
                    Math.toIntExact(Math.round(-Math.cos(angle) * radius)),
                    result.interaction.getName()
            );
        }

        if (!EIClientConfig.HANDLER.instance().displayFailed) {
            failedResults = List.of();
        }

        //EICommon.LOG.info("Baked {}", Arrays.toString(successful));
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        int mouseX = Math.toIntExact(Math.round(pMouseX));
        int mouseY = Math.toIntExact(Math.round(pMouseY));
        int centerX = minecraft.getWindow().getGuiScaledWidth() / 2;
        int centerY = minecraft.getWindow().getGuiScaledHeight() / 2;

        int closestDistance = Integer.MAX_VALUE;
        int closest = -1;

        for (int i = 0; i < successful.length; i++) {
            int dx = (centerX + successful[i].x) - mouseX;
            int dy = (centerY + successful[i].y) - mouseY;
            int ds = dx * dx + dy * dy;
            if (ds < closestDistance) {
                closestDistance = ds;
                closest = i;
            }
        }

        if (closestDistance < 400) {
            selectedInteraction = closest;
        } else {
            selectedInteraction = -1;
        }
    }

    @Override
    //? if >=1.21.11 {
    /*public boolean mouseClicked(@NotNull net.minecraft.client.input.MouseButtonEvent event, boolean arg1) {
    *///? } else {
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
    //? }
        //? if >=1.21.11 {
        /*if (event.button() == 0 && selectedInteraction != -1 && serverReplied) {
        *///?} else
        if (button == 0 && selectedInteraction != -1 && serverReplied) {
            final ExtInteraction interaction = successfulResults.get(selectedInteraction).interaction;
            EICommon.getPlatform().sendToServer(new RunExtInteractionPacket(
                    target.getEither(), interaction.getId()
            ));
            EIClient.scheduleClient(() -> {
                interaction.handleExecution(Minecraft.getInstance().player, target);
            });
            onClose();
            return true;
        }

        //? if >=1.21.11 {
        /*return super.mouseClicked(event, arg1);
         *///? } else
        return super.mouseClicked(mouseX, mouseY, button);
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

    @Override
    public void renderBackground(@NotNull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        // NO OP
    }

    public void handleMenuResult(MenuResultPacket packet) {
        serverReplied = true;
        successfulResults = packet.good();
        failedResults = packet.bad();
        bake();
    }

    record ActionData(ExtInteractionIcon icon, int x, int y, Component name) { }
}
