package com.denisjava.extended_interactions.impl;

import net.minecraft.client.gui.GuiGraphics;

public interface ExtInteractionIcon {
    /**
     * Draws icon
     * @param g Graphics context to draw on
     * @param x X-coordinate of icon's left-top corner
     * @param y Y-coordinate of icon's left-top corner
     * @param width Icon's width
     * @param height Icon's height
     */
    void render(GuiGraphics g, int x, int y, int width, int height);
}
