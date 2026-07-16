package com.denisjava.extended_interactions.api.providers;

import com.denisjava.extended_interactions.api.ExtInteraction;
import com.denisjava.extended_interactions.api.InteractionArgument;
import com.denisjava.extended_interactions.client.InteractionRadialMenuButton;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import com.denisjava.extended_interactions.impl.RadialMenuButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SuccessfulResult extends EIResult implements RadialMenuButton, InteractionRadialMenuButton {
    private @Nullable Component nameOverride;
    private @Nullable String iconOverride;
    private @Nullable List<InteractionArgument> arguments;

    @ApiStatus.Internal
    public SuccessfulResult(@NotNull ExtInteraction interaction, @Nullable Component nameOverride,
                            @Nullable String iconOverride, @Nullable List<InteractionArgument> arguments) {
        super(interaction);
        this.nameOverride = nameOverride;
        this.iconOverride = iconOverride;
        this.arguments = arguments;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType") // this is a codec constructor
    @ApiStatus.Internal
    public SuccessfulResult(ExtInteraction interaction, Optional<Component> nameOverride,
                            Optional<String> iconOverride, Optional<List<InteractionArgument>> arguments) {
        this(interaction, nameOverride.orElse(null), iconOverride.orElse(null), arguments.orElse(null));
    }

    public SuccessfulResult addName(Component name) {
        nameOverride = name;
        return this;
    }

    public SuccessfulResult addIconOverride(String icon) {
        iconOverride = icon;
        return this;
    }

    public SuccessfulResult addArguments(List<InteractionArgument> arguments) {
        if (arguments.isEmpty()) throw new IllegalArgumentException("List of arguments must not be empty. If no arguments are needed use null instead!");
        this.arguments = arguments;
        return this;
    }

    public @Nullable Component getNameOverride() {
        return nameOverride;
    }

    public @Nullable String getIconOverride() {
        return iconOverride;
    }

    public @Nullable List<InteractionArgument> getArguments() {
        return arguments;
    }

    //<editor-fold desc="EI Internal Code" defaultstate="collapsed">
    @ApiStatus.Internal
    public Optional<Component> getOptionalNameOverride() {
        return Optional.ofNullable(nameOverride);
    }

    @ApiStatus.Internal
    public Optional<String> getOptionalIconOverride() {
        return Optional.ofNullable(iconOverride);
    }

    @ApiStatus.Internal
    public Optional<List<InteractionArgument>> getOptionalArguments() {
        return Optional.ofNullable(arguments);
    }

    @ApiStatus.Internal
    @Override
    public Component getName() {
        return nameOverride == null ? interaction.getName() : nameOverride;
    }

    @ApiStatus.Internal
    @Override
    public ExtInteractionIcon getIcon() {
        return iconOverride == null ? interaction.getIcon() : interaction.getIcon(iconOverride);
    }

    @ApiStatus.Internal
    @Override
    public boolean isClientSide() {
        return interaction.isClientSide();
    }

    @ApiStatus.Internal
    @Override
    public ResourceLocation getId() {
        return interaction.getId();
    }

    @ApiStatus.Internal
    @Override
    public @Nullable InteractionArgument getArgument() {
        return null;
    }

    @ApiStatus.Internal
    @Override
    public List<Component> _getDebugInfo() {
        return List.of(
                Component.literal("Interaction:"),
                Component.literal("   id=" + interaction.getId()),
                Component.literal("   plugin=" + interaction.getDeclaringPlugin().getUID()),
                Component.literal("Icon Override: " + iconOverride),
                Component.literal("Arguments: " + arguments),
                Component.literal("Name Override: (next line)"),
                nameOverride == null ? Component.literal("**NO OVERRIDE**") : nameOverride,
                Component.empty()
        );
    }
    //</editor-fold>
}
