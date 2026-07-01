package com.denisjava.extended_interactions.impl;

import com.denisjava.extended_interactions.api.ExtInteraction;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * See {@link com.denisjava.extended_interactions.api.EIResults}
 */
public class EIResultImpl {
    /**
     * Base class for all provider results.<br>
     * Constructor is private to prevent any disallowed subclasses.
     */
    public abstract static class Result {
        private Result() {}
    }

    @ApiStatus.Internal
    public interface NonEmptyResult {
        ExtInteraction getInteraction();
    }

    public final static class Empty extends Result {
        @ApiStatus.Internal
        public Empty() {}
    }

    public final static class Successful extends Result implements NonEmptyResult {
        public final ExtInteraction interaction;
        public final @Nullable String iconOverride;

        @ApiStatus.Internal
        public Successful(ExtInteraction interaction, @Nullable String iconOverride) {
            this.interaction = interaction;
            this.iconOverride = iconOverride;
        }

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        @ApiStatus.Internal
        public Successful(ExtInteraction interaction, @NotNull Optional<String> iconOverride) {
            this.interaction = interaction;
            this.iconOverride = iconOverride.orElse(null);
        }

        @Override
        public ExtInteraction getInteraction() {
            return interaction;
        }

        public @Nullable String getIconOverride() {
            return iconOverride;
        }

        public @NotNull Optional<String> getOptionalIconOverride() {
            return Optional.ofNullable(iconOverride);
        }
    }

    public final static class SilentlyFailed extends Result implements NonEmptyResult {
        public final ExtInteraction interaction;

        @ApiStatus.Internal
        public SilentlyFailed(ExtInteraction interaction) {
            this.interaction = interaction;
        }

        @Override
        public ExtInteraction getInteraction() {
            return interaction;
        }
    }

    public final static class Failed extends Result implements NonEmptyResult {
        public final ExtInteraction interaction;
        public final Component error;

        @ApiStatus.Internal
        public Failed(ExtInteraction interaction, Component error) {
            this.interaction = interaction;
            this.error = error;
        }

        @Override
        public ExtInteraction getInteraction() {
            return interaction;
        }

        public Component getError() {
            return error;
        }
    }
}
