//? if oracle_index {
package com.denisjava.extended_interactions.compat.oracleindex;

import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.JavaInteraction;
import com.denisjava.extended_interactions.api.providers.EIBlockProvider;
import com.denisjava.extended_interactions.api.providers.EIResult;
import com.denisjava.extended_interactions.api.providers.EIResultCollector;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import com.denisjava.extended_interactions.util.ThrowableEIResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class OracleIndexLookupInteraction extends JavaInteraction implements EIBlockProvider {
    public OracleIndexLookupInteraction(ResourceLocation id, ExtInteractionIcon icon, EIPlugin declaringPlugin) {
        super(id, icon, declaringPlugin);
    }

    @Override
    public void handleBlockExecution(Player player, Level level, BlockPos pos, BlockState state, String argumentId) {
        if (level.isClientSide()) {
            ResourceLocation key = BuiltInRegistries.ITEM.getKey(state.getBlock().asItem());
            OracleIndexCompatClient.openPageFor(key);
        }
    }

    @Override
    public void collectForBlock(EIResultCollector collector, Level level, Player user, BlockPos pos, BlockState state) throws ThrowableEIResult {
        if (level.isClientSide()) {
            ResourceLocation key = BuiltInRegistries.ITEM.getKey(state.getBlock().asItem());
            if (OracleIndexCompatClient.hasPageFor(key)) {
                collector.add(EIResult.success(this));
            }
        }
    }

    @Override
    public boolean isClientSide() {
        return true;
    }
}
//? }