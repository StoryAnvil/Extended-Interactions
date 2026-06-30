package com.denisjava.extended_interactions.api;

import com.denisjava.extended_interactions.impl.EIResultImpl;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

public final class EIResults {

    /**
     * Empty provider result. This acts like provider did not exist at all.<br>
     * Using this is not recommended in most cases. See {@link EIResults#silentFailure(ExtInteraction)} instead
     */
    @ApiStatus.Experimental
    public static final EIResultImpl.Result EMPTY = new EIResultImpl.Empty();

    /**
     * Successful provider result.
     * @param interaction Interaction provided.
     */
    public static EIResultImpl.Result success(ExtInteraction interaction) {
        return new EIResultImpl.Successful(interaction);
    }

    /**
     * Silent provider failure. This is not displayed to player.
     * @param interaction Interaction usually provided by the provider.
     */
    public static EIResultImpl.Result silentFailure(ExtInteraction interaction) {
        return new EIResultImpl.SilentlyFailed(interaction);
    }

    /**
     * Failed provider result.
     * @param interaction Interaction that can not be used.
     * @param error Reason why interaction can not be used.
     */
    public static EIResultImpl.Result failure(ExtInteraction interaction, Component error) {
        return new EIResultImpl.Failed(interaction, error);
    }
}
