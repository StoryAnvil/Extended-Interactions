package com.denisjava.extended_interactions.config;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.network.chat.Component;

public enum ExtInteractionState implements NameableEnum {
    DEFAULT, HIDE_FAILURES, HIDE;

    @Override
    public Component getDisplayName() {
        return Component.translatable("extended_interactions.eis." + name().toLowerCase());
    }
}
