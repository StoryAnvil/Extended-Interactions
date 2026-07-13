package com.denisjava.extended_interactions.client;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.config.EIClientConfig;
import com.denisjava.extended_interactions.config.ExtInteractionState;
import com.denisjava.extended_interactions.impl.EIResultImpl;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import com.denisjava.extended_interactions.impl.MenuTarget;
import com.denisjava.extended_interactions.impl.RadialMenuButton;
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
    private static final ResourceLocation NO_OPTIONS_SPRITE = EICommon.id("no");
    private final Component noOptions = Component.translatable("gui.extended_interactions.no_options");

    RadialMenuData data;
    private List<RadialMenuButton> original;

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
    protected void init() {
        // Predict interactions on client
        EIClientConfig config = EIClientConfig.HANDLER.instance();
        if (config.predictInteractions) {
            Pair<RadialMenuData, List<RadialMenuButton>> p = RadialMenuData.createPrediction(target);
            data = p.getFirst();
            original = p.getSecond();
        } else {
            data = RadialMenuData.createEmpty();
            original = List.of();
        }
        onDataUpdate();

        // Request interactions from server
        EICommon.getPlatform().sendToServer(target.createRequest());
    }

    @Override
    public void render(@NotNull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        int centerX = g.guiWidth() / 2;
        int centerY = g.guiHeight() / 2;

        if (successful.length == 0) {
            //? if >=1.21.11
            //g.blitSprite(RenderPipelines.GUI_TEXTURED, NO_OPTIONS_SPRITE, centerX - 13, centerY - 13, 26, 26);
            //? if <1.21.11
            g.blitSprite(NO_OPTIONS_SPRITE, centerX - 13, centerY - 13, 26, 26);
            g.drawString(font, noOptions, centerX - font.width(noOptions) / 2, centerY + 26, -1);
        }

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

    /**
     * Prepares rendering data for current {@link RadialMenuScreen#data}
     */
    void onDataUpdate() {
        EIClientConfig config = EIClientConfig.HANDLER.instance();
        final int radius = config.radialMenuRadius;
        Map<String, ExtInteractionState> stateMap = config.interactions;

        legacy: {
            if (true) break legacy;
            successfulResults = successfulResults.stream().filter(s ->
                    stateMap.get(s.interaction.getId().toString()) != ExtInteractionState.HIDE).toList();
            failedResults = failedResults.stream().filter(s ->
                            stateMap.getOrDefault(s.interaction.getId().toString(), ExtInteractionState.DEFAULT) == ExtInteractionState.DEFAULT)
                    .filter(config::failureFilter).toList();

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
        }

        successful = new ActionData[data.getButtons().size()];
        for (int i = 0; i < successful.length; i++) {
            double angle = (float) i / successful.length * Math.PI * 2;
            RadialMenuButton button = data.getButtons().get(i);
            successful[i] = new ActionData(
                    button.getIcon(),
                    Math.toIntExact(Math.round(Math.sin(angle) * radius)),
                    Math.toIntExact(Math.round(-Math.cos(angle) * radius)),
                    button.getName()
            );
        }

        if (!config.displayFailed) {
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
    //? if <1.21.11 {
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (EIClient.OPEN_RADIAL.get().matches(keyCode, scanCode)) {
    //?} else {
    /*public boolean keyReleased(@NotNull net.minecraft.client.input.KeyEvent event) {
        if (EIClient.OPEN_RADIAL.get().matches(event)) {
    *///? }
            submit();
            return true;
        }

        //? if >=1.21.11
        //return super.keyReleased(event);
        //? if <1.21.11
        return super.keyReleased(keyCode, scanCode, modifiers);
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
            submit();
            return true;
        }

        //? if >=1.21.11 {
        /*return super.mouseClicked(event, arg1);
         *///? } else
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void submit() {
        if (selectedInteraction != -1) {
            RadialMenuButton button = data.getButtons().get(selectedInteraction);

            // Execute as a ExtInteraction
            if (button instanceof EIResultImpl.Successful s) {
                if (!s.isClientSide())
                    EICommon.getPlatform().sendToServer(new RunExtInteractionPacket(
                            target.getEither(), s.interaction.getId()
                    ));
                EIClient.scheduleClient(() -> {
                    s.interaction.handleExecution(Minecraft.getInstance().player, target);
                });
            }
            if (button instanceof ClientRadialMenuButton c) {
                c.executeClientSide(this);
            } else onClose();
        } else onClose();
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

    /**
     * Called when {@link MenuResultPacket} is received while this screen is opened
     */
    public void handleMenuResult(MenuResultPacket packet) {
        serverReplied = true;
        data = RadialMenuData.createMerged(original, packet.good(), packet.bad());
        original = null;
        onDataUpdate();
    }

    record ActionData(ExtInteractionIcon icon, int x, int y, Component name) { }
}
