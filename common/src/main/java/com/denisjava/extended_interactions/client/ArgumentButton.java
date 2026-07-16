package com.denisjava.extended_interactions.client;

import com.denisjava.extended_interactions.api.ExtInteraction;
import com.denisjava.extended_interactions.api.InteractionArgument;
import com.denisjava.extended_interactions.api.providers.SuccessfulResult;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import com.denisjava.extended_interactions.impl.RadialMenuButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class ArgumentButton implements RadialMenuButton, InteractionRadialMenuButton {
    private final SuccessfulResult result;
    private final InteractionArgument argument;

    public ArgumentButton(SuccessfulResult result, InteractionArgument argument) {
        this.result = result;
        this.argument = argument;
    }

    @Override
    public Component getName() {
        return argument.nameOverride().orElse(result.getName());
    }

    @Override
    public ExtInteractionIcon getIcon() {
        return argument.iconOverride().isPresent() ? result.getInteraction().getIcon(argument.iconOverride().get())
                : result.getIcon();
    }

    @Override
    public boolean isClientSide() {
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return result.getInteraction().getId();
    }

    @Override
    public ExtInteraction getInteraction() {
        return result.getInteraction();
    }

    @Override
    public @Nullable InteractionArgument getArgument() {
        return argument;
    }

    @Override
    public String toString() {
        return "Argument[" + result.getInteraction().getId() + "=" + argument.id() + "]";
    }

    @Override
    public List<Component> _getDebugInfo() {
        return Stream.concat(Stream.concat(Stream.of((Component) Component.literal("=== Begin Stored Result == ")), result._getDebugInfo().stream()),
                Stream.of(
                        Component.literal("Argument:"),
                        Component.literal("  id=" + argument.id()),
                        Component.literal("  iconOverride=" + argument.iconOverride().orElse("null")),
                        Component.literal("  nameOverride=").append(argument.nameOverride().orElse(Component.literal("**NO OVERRIDE**")))
                )
            ).toList();
    }
}
