package com.denisjava.extended_interactions.config;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.ExtInteraction;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import com.denisjava.extended_interactions.impl.MenuTarget;
import com.denisjava.extended_interactions.util.ThrowableEIResult;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.stream.Stream;

public class DataDrivenInteraction extends ExtInteraction {
    public static final Codec<InteractionTemplate> INTERACTION_TEMPLATE_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ItemStack.CODEC.fieldOf("icon").forGetter(InteractionTemplate::icon),
            DataDrivenProviders.BLOCK_PROVIDER_CODEC.listOf().fieldOf("blocks").forGetter(InteractionTemplate::blockProviders),
            DataDrivenProviders.ENTITY_PROVIDER_CODEC.listOf().fieldOf("entities").forGetter(InteractionTemplate::entityProviders),
            DataDrivenAction.CODEC.listOf().fieldOf("actions").forGetter(InteractionTemplate::actions)
    ).apply(inst, InteractionTemplate::new));

    private final List<DataDrivenAction> actions;
    private DataDrivenInteraction(ResourceLocation id, ExtInteractionIcon icon, EIPlugin declaringPlugin, List<DataDrivenAction> actions) {
        super(id, icon, declaringPlugin);
        this.actions = actions;
    }

    @Override
    public void handleExecution(Player player, MenuTarget target, String argumentId) {
        for (DataDrivenAction action : actions) {
            action.handle(player, target);
        }
    }

    @Override
    public String toString() {
        return "DataDrivenInteraction{" +
                "id=" + id +
                ", actions=" + actions +
                '}';
    }

    public void test(Player player) throws ThrowableEIResult {
        for (DataDrivenAction action : actions) {
            action.test(this, player);
        }
    }

    public record InteractionTemplate(
            ItemStack icon,
            List<DataDrivenProviders.BlockProvider> blockProviders,
            List<DataDrivenProviders.EntityProvider> entityProviders,
            List<DataDrivenAction> actions
    ) {
        public Pair<DataDrivenInteraction, List<DataDrivenProviders.ProviderRegistrar>> build(String name, EIPlugin declaringPlugin) {
            DataDrivenInteraction interaction = new DataDrivenInteraction(
                    EICommon.id("custom/" + name),
                    new ExtInteractionIcon.ItemStackIcon(icon),
                    declaringPlugin, actions

            );
            List<DataDrivenProviders.ProviderRegistrar> registrars = Stream.concat(blockProviders.stream().map(e -> (DataDrivenProviders.ProviderRegistrar) e),
                    entityProviders.stream().map(e -> (DataDrivenProviders.ProviderRegistrar) e)).toList();
            return new Pair<>(interaction, registrars);
        }
    }
}
