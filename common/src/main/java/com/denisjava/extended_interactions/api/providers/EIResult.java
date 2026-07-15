package com.denisjava.extended_interactions.api.providers;

import com.denisjava.extended_interactions.api.ExtInteraction;
import com.denisjava.extended_interactions.util.ThrowableEIResult;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for all provider results.<br>
 * DO NOT EXTEND!
 */
@ApiStatus.NonExtendable
public abstract class EIResult {
    protected final ExtInteraction interaction;

    EIResult(@NotNull ExtInteraction interaction) {
        this.interaction = interaction;
    }

    /**
     * @return {@link ExtInteraction} this result if for
     */
    public @NotNull ExtInteraction getInteraction() {
        return interaction;
    }

    /**
     * Throws this result as a {@link ThrowableEIResult} immediately.
     * @throws ThrowableEIResult always thrown
     */
    @Contract("-> fail")
    public void throwNow() throws ThrowableEIResult {
        throw new ThrowableEIResult(this);
    }

    /**
     * Creates a successful provider result.<br>
     * For icon overrides use {@link SuccessfulResult#addIconOverride(String)}.
     */
    public static SuccessfulResult success(ExtInteraction interaction) {
        return new SuccessfulResult(interaction, (Component) null, null, null);
    }

    /**
     * Creates a failed provider result.<br>
     * By default, this kind of failure is not displayed.<br>
     * To make it visible add a reason with {@link FailedResult#addCodelessReason}
     */
    public static FailedResult fail(ExtInteraction interaction) {
        return new FailedResult(interaction, (Component) null, null);
    }
}
