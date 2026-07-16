//? if patchouli {
package com.denisjava.extended_interactions.compat.patchouli;

import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import vazkii.patchouli.client.book.BookIcon;

public record PatchouliBookIcon(BookIcon icon) implements ExtInteractionIcon {
    @Override
    public void render(GuiGraphics g, Font font, int x, int y, int width, int height) {
        icon.render(g, x, y);
    }
}
//? }