package com.denisjava.extended_interactions.api;

import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import com.denisjava.extended_interactions.impl.MenuTarget;
import com.denisjava.extended_interactions.util.ThrowableEIResult;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    public final Identifier getId() {
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
     * Returns icon for provided override name.<br>
     * Use {@link com.denisjava.extended_interactions.api.providers.SuccessfulResult#addIconOverride(String)} in your provider to supply icon override name and
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

    /**
     * @return whether this interaction is client-side or not. If <code>true</code>, it won't be sent to server by any means.
     */
    public boolean isClientSide() {
        return false;
    }

    public interface SimpleProvider {
        void providerCheck(Player player) throws ThrowableEIResult;
    }
    public interface EntityProvider {
        void providerCheck(Player player, Entity target) throws ThrowableEIResult;
    }
}
