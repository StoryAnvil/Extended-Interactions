package com.denisjava.extended_interactions.impl;

import com.denisjava.extended_interactions.api.ExtInteraction;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

public class EIResultImpl {
    public abstract static class Result {
        private Result() {}
    }

    public static class Empty extends Result {
        @ApiStatus.Internal
        public Empty() {}
    }

    public static class Successful extends Result {
        public final ExtInteraction interaction;

        @ApiStatus.Internal
        public Successful(ExtInteraction interaction) {
            this.interaction = interaction;
        }

        public ExtInteraction getInteraction() {
            return interaction;
        }
    }

    public static class SilentlyFailed extends Result {
        public final ExtInteraction interaction;

        @ApiStatus.Internal
        public SilentlyFailed(ExtInteraction interaction) {
            this.interaction = interaction;
        }
    }

    public static class Failed extends Result {
        public final ExtInteraction interaction;
        public final Component error;

        @ApiStatus.Internal
        public Failed(ExtInteraction interaction, Component error) {
            this.interaction = interaction;
            this.error = error;
        }

        public ExtInteraction getInteraction() {
            return interaction;
        }

        public Component getError() {
            return error;
        }
    }
}
