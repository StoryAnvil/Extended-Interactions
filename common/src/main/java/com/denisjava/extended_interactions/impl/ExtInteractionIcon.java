package com.denisjava.extended_interactions.impl;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public interface ExtInteractionIcon {
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
            g.pose().pushMatrix();
            g.pose().scale((float) width / 16, (float) height / 16);
            render16x16(g, font, x, y);
            g.pose().popMatrix();
            //? } else {
            /*g.pose().pushPose();
            g.pose().scale((float) width / 16, (float) height / 16, 1);
            render16x16(g, font, x, y);
            g.pose().popPose();
            *///? }
        }

        @Override
        public void render16x16(GuiGraphics g, Font font, int x, int y) {
            g.renderItem(stack, x, y);
            g.renderItemDecorations(font, stack, x, y);
        }
    }
}
