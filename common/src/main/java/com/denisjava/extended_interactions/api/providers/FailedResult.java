package com.denisjava.extended_interactions.api.providers;

import com.denisjava.extended_interactions.api.ExtInteraction;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FailedResult extends EIResult {
    private @Nullable Component reason;
    private @Nullable String errorCode;

    @ApiStatus.Internal
    public FailedResult(@NotNull ExtInteraction interaction, @Nullable Component reason, @Nullable String errorCode) {
        super(interaction);
        this.reason = reason;
        this.errorCode = errorCode;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType") // this is a codec constructor
    @ApiStatus.Internal
    public FailedResult(@NotNull ExtInteraction interaction, @NotNull Optional<Component> reason, @NotNull Optional<String> errorCode) {
        this(interaction, reason.orElse(null), errorCode.orElse(null));
    }

    public FailedResult addReason(Component reason, String errorCode) {
        this.reason = reason;
        this.errorCode = errorCode;
        return this;
    }

    public FailedResult addReason(String errorCode) {
        this.reason = Component.translatable(interaction.getId().toLanguageKey("extinter", errorCode));
        this.errorCode = errorCode;
        return this;
    }

    @ApiStatus.Experimental
    public FailedResult addCodelessReason(Component reason) {
        this.reason = reason;
        return this;
    }

    public @Nullable Component getReason() {
        return reason;
    }

    public @Nullable String getErrorCode() {
        return errorCode;
    }

    @ApiStatus.Internal
    public @NotNull Optional<Component> getOptionalReason() {
        return Optional.ofNullable(reason);
    }

    @ApiStatus.Internal
    public @NotNull Optional<String> getOptionalErrorCode() {
        return Optional.ofNullable(errorCode);
    }
}
