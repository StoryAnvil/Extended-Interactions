package com.denisjava.extended_interactions.impl;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface RadialMenuButton {
    Component getName();
    ExtInteractionIcon getIcon();
    boolean isClientSide();
    ResourceLocation getId();

    default @Nullable String _categoryNameHook() {
        return null;
    }
}
