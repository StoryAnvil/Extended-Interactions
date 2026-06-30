package com.denisjava.extended_interactions.api;

import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

/**
 * Base class for all extended interactions.<br>
 * <b>DO NOT SUBCLASS IT! Use {@link JavaInteraction} to implement interactions with java.</b>
 */
@ApiStatus.NonExtendable
public abstract class ExtInteraction {
    protected final Identifier id;
    protected final ExtInteractionIcon icon;
    protected final EIPlugin declaringPlugin;

    protected ExtInteraction(Identifier id, ExtInteractionIcon icon, EIPlugin declaringPlugin) {
        this.id = id;
        this.icon = icon;
        this.declaringPlugin = declaringPlugin;
    }

    public Identifier getId() {
        return id;
    }

    public ExtInteractionIcon getIcon() {
        return icon;
    }

    public final MutableComponent getName() {
        return Component.translatable(id.toLanguageKey("extinter"));
    }

    /**
     * Subclass {@link JavaInteraction} instead of {@link ExtInteraction}
     */
    abstract void _totally_not_sus_method_preventing_bad_subclasses_();
}
