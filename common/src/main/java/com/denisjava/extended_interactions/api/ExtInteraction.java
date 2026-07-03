package com.denisjava.extended_interactions.api;

import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import com.denisjava.extended_interactions.impl.MenuTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

/**
 * Base class for all extended interactions.<br>
 * <b>DO NOT SUBCLASS IT! Use {@link JavaInteraction} to implement interactions with java.</b>
 */
@ApiStatus.NonExtendable
public abstract class ExtInteraction {
    protected final ResourceLocation id;
    protected final ExtInteractionIcon icon;
    protected final EIPlugin declaringPlugin;

    protected ExtInteraction(ResourceLocation id, ExtInteractionIcon icon, EIPlugin declaringPlugin) {
        this.id = id;
        this.icon = icon;
        this.declaringPlugin = declaringPlugin;
    }

    public final ResourceLocation getId() {
        return id;
    }

    public final ExtInteractionIcon getIcon() {
        return icon;
    }

    public final MutableComponent getName() {
        return Component.translatable(id.toLanguageKey("extinter"));
    }

    public EIPlugin getDeclaringPlugin() {
        return declaringPlugin;
    }

    /**
     * Returns icon for provided override name ({@link EIResults#success(ExtInteraction, String)}.<br>
     * Use {@link EIResults#success(ExtInteraction, String)} in your provider to supply icon override name and
     * return correct icon for the name in this method.
     * @param overrideName Dev-defined name for icon
     * @return Icon to use instead of default one
     */
    public ExtInteractionIcon getIcon(String overrideName) {
        return icon;
    }

    /**
     * Handles execution of this extended interaction.<br>
     * If you are using {@link JavaInteraction}, override {@link JavaInteraction#handleBlockExecution(Player, Level, BlockPos, BlockState)}, {@link JavaInteraction#handleEntityExecution(Player, Level, Entity)} instead
     * @param player Player who executed the interaction
     * @param target Interaction's target
     */
    public abstract void handleExecution(Player player, MenuTarget target);
}
