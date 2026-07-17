package com.denisjava.extended_interactions.impl;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

//? if >=1.21.11
//import net.minecraft.client.renderer.RenderPipelines;

public interface ExtInteractionIcon {
    ExtInteractionIcon ERROR_ICON = new ItemStackIcon(Items.BARRIER);

    /**
     * Draws icon
     * @param g Graphics context to draw on
     * @param x X-coordinate of icon's left-top corner
     * @param y Y-coordinate of icon's left-top corner
     * @param width Icon's width
     * @param height Icon's height
     */
    void render(GuiGraphics g, Font font, int x, int y, int width, int height);
    default void render16x16(GuiGraphics g, Font font, int x, int y) {
        render(g, font, x, y, 16, 16);
    }

    record ItemStackIcon(ItemStack stack) implements ExtInteractionIcon {
        public ItemStackIcon(ItemLike item) {
            this(new ItemStack(item));
        }

        @Override
        public void render(GuiGraphics g, Font font, int x, int y, int width, int height) {
            //? if >=1.21.11 {
            /*g.pose().pushMatrix();
            g.pose().scale((float) width / 16, (float) height / 16);
            render16x16(g, font, x, y);
            g.pose().popMatrix();
            *///? } else {
            g.pose().pushPose();
            g.pose().scale((float) width / 16, (float) height / 16, 1);
            render16x16(g, font, x, y);
            g.pose().popPose();
            //? }
        }

        @Override
        public void render16x16(GuiGraphics g, Font font, int x, int y) {
            g.renderItem(stack, x, y);
            g.renderItemDecorations(font, stack, x, y);
        }
    }

    record ComponentIcon(Component text) implements ExtInteractionIcon {
        public ComponentIcon(String text, int color) {
            this(Component.literal(text).withColor(color));
        }

        @Override
        public void render(GuiGraphics g, Font font, int x, int y, int width, int height) {
            int q = font.width(text);
            float scale = Math.min((float) width / q, (float) height / 12);
            float hs = scale * 0.5F;
            //? if >=1.21.11 {
            /*g.pose().pushMatrix();
            g.pose().translate(x + (float) width / 2 - q * hs, y + (float) height / 2 - 9 * hs);
            g.pose().scale(scale, scale);
            *///? } else {
            g.pose().pushPose();
            g.pose().translate(x + (float) width / 2 - q * hs, y + (float) height / 2 - 9 * hs, 0);
            g.pose().scale(scale, scale, 1);
            //? }
            g.drawString(font, text, 0, 0, -1);
            //? if >=1.21.11 {
            /*g.pose().popMatrix();
            *///? } else {
            g.pose().popPose();
            //? }
        }
    }

    record TexturedSpriteIcon(ResourceLocation texture) implements ExtInteractionIcon {
        @Override
        public void render(GuiGraphics g, Font font, int x, int y, int width, int height) {
            //? if >=1.21.11
            //g.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, width, height);
            //? if <1.21.11
            g.blitSprite(texture, x, y, width, height);
        }
    }

    record StackedIcon(ExtInteractionIcon icon1, ExtInteractionIcon icon2) implements ExtInteractionIcon {
        @Override
        public void render(GuiGraphics g, Font font, int x, int y, int width, int height) {
            //? if >=1.21.11 {
            /*g.pose().pushMatrix();
            //TODO
            *///? } else {
            g.pose().pushPose();
            g.pose().translate(0, 0, 10);
            //? }

            icon1.render(g, font, x, y, width, height);
            icon2.render(g, font, x, y, width, height);

            //? if >=1.21.11 {
            /*g.pose().popMatrix();
            *///? } else {
            g.pose().popPose();
            //? }
        }

        @Override
        public void render16x16(GuiGraphics g, Font font, int x, int y) {
            icon1.render16x16(g, font, x, y);
            icon2.render16x16(g, font, x, y);
        }
    }
}
