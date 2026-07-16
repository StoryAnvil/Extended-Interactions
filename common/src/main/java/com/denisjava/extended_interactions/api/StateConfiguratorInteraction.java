package com.denisjava.extended_interactions.api;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.providers.EIBlockProvider;
import com.denisjava.extended_interactions.api.providers.EIResult;
import com.denisjava.extended_interactions.api.providers.EIResultCollector;
import com.denisjava.extended_interactions.api.providers.SuccessfulResult;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import com.denisjava.extended_interactions.util.ThrowableEIResult;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class StateConfiguratorInteraction<T extends Comparable<T>> extends JavaInteraction {
    private final Property<T> property;
    public StateConfiguratorInteraction(ResourceLocation id, ExtInteractionIcon icon, Property<T> property, EIPlugin declaringPlugin) {
        super(id, icon, declaringPlugin);
        this.property = property;
    }

    @Override
    public void handleBlockExecution(Player player, Level level, BlockPos pos, BlockState state, String argumentId) {
        Optional<T> value = property.getValue(argumentId);
        if (value.isPresent()) {
            level.setBlock(pos, state.setValue(property, value.get()), 2);
        } else {
            EICommon.LOG.warn("Invalid StateConfiguratorArgument {} for property \"{}\"({})", argumentId, property.getName(), property.getClass().getCanonicalName());
        }
    }

    /**
     * Creates a {@link SuccessfulResult} with arguments for this interaction in providers.
     * @param player Player passed to the provider
     */
    public final SuccessfulResult success(Player player) {
        return EIResult.success(this)
                .addArguments(getAvailableValues(player).stream()
                        .map(w -> buildArgument(property.getName(w), w))
                        .toList());
    }

    /**
     * Simple {@link EIBlockProvider} for this interaction.
     */
    public void blockProvider(EIResultCollector collector, Level level, Player player, BlockPos pos, BlockState state) throws ThrowableEIResult {
        //noinspection DataFlowIssue
        //? if >=1.21.11
        //if (player.gameMode().isBlockPlacingRestricted()) {
        //? if <1.21.11
        if (!player.getAbilities().mayBuild) {
            collector.add(EIResult.fail(this));
            return;
        }
        collector.add(success(player));
    }

    /**
     * Collects list of all possible values player can configure.
     * @param player Player who requested this interaction
     */
    //? if <1.21.11
    public Collection<T> getAvailableValues(Player player) {
    //? if >=1.21.11
    //public List<T> getAvailableValues(Player player) {
        return property.getPossibleValues();
    }

    /**
     * Builds argument for possible value.
     * @param id ID for created {@link InteractionArgument}
     * @param value Property value to build argument for
     */
    public InteractionArgument buildArgument(String id, T value) {
        return new InteractionArgument(id,
                Optional.of(Component.literal(property.getName(value))),
                Optional.empty());
    }

    public Property<T> getProperty() {
        return property;
    }
}
