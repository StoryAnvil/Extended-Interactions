package com.denisjava.extended_interactions.compat;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.JavaInteraction;
import com.denisjava.extended_interactions.api.providers.EIBlockProvider;
import com.denisjava.extended_interactions.api.providers.EIResult;
import com.denisjava.extended_interactions.api.providers.EIResultCollector;
import com.denisjava.extended_interactions.api.providers.SuccessfulResult;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import com.denisjava.extended_interactions.util.EIUtils;
import com.denisjava.extended_interactions.util.ThrowableEIResult;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

import static com.denisjava.extended_interactions.EICommon.id;

public class TakeResultInteraction extends JavaInteraction implements EIBlockProvider {
    private final ExtInteractionIcon.TexturedSpriteIcon icon_small = new ExtInteractionIcon.TexturedSpriteIcon(id("take_small"));
    public TakeResultInteraction(ResourceLocation id, EIPlugin declaringPlugin) {
        super(id, new ExtInteractionIcon.TexturedSpriteIcon(id("take")), declaringPlugin);
    }

    @Override
    public void handleBlockExecution(Player player, Level level, BlockPos pos, BlockState state, String argumentId) {
        if (level.isClientSide()) return;

        Function<ItemStack, ItemStack> consumer = stack ->
                player.getInventory().add(stack) ? ItemStack.EMPTY : stack;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof EIContainer container) {
            if (!container.ei$canTakeResult(player) || !container.ei$hasResult(player)) {
                EICommon.LOG.warn("Failed to take result from {} as a BlockEntity", pos);
                return;
            }
            container.ei$takeResult((ServerPlayer) player, consumer);
            return;
        }

        if (be != null) return;
        // If block is not a BlockEntity check for EIContainer.BlockContainer
        if (state.getBlock() instanceof EIContainer.BlockContainer container) {
            if (!container.ei$canTakeResult(player, level, pos, state) || !container.ei$hasResult(player, level, pos, state)) {
                EICommon.LOG.warn("Failed to take result from {} as a Block", pos);
                return;
            }
            container.ei$takeResult((ServerPlayer) player, consumer, level, pos, state);
        }
    }

    @Override
    public void collectForBlock(EIResultCollector collector, Level level, Player user, BlockPos pos, BlockState state) throws ThrowableEIResult {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof EIContainer container) {
            if (!container.ei$canTakeResult(user)) EIResult.fail(this).addReason("no_perm").throwNow();
            if (!container.ei$hasResult(user)) EIResult.fail(this).addReason("empty").throwNow();

            SuccessfulResult result = EIResult.success(this);
            ItemStack preview = container.ei$getPreview(user);
            if (preview != null) result.addIconOverride(EIUtils.encodeToString(preview, ItemStack.CODEC));
            collector.add(result);
            return;
        }

        if (be != null) return;
        // If block is not a BlockEntity check for EIContainer.BlockContainer
        if (state.getBlock() instanceof EIContainer.BlockContainer container) {
            if (!container.ei$canTakeResult(user, level, pos, state)) EIResult.fail(this).addReason("no_perm").throwNow();
            if (!container.ei$hasResult(user, level, pos, state)) EIResult.fail(this).addReason("empty").throwNow();

            SuccessfulResult result = EIResult.success(this);
            ItemStack preview = container.ei$getPreview(user, level, pos, state);
            if (preview != null) result.addIconOverride(EIUtils.encodeToString(preview, ItemStack.CODEC));
            collector.add(result);
        }
    }

    @Override
    public ExtInteractionIcon getIcon(String overrideName) {
        ItemStack stack = EIUtils.decodeFromString(overrideName, ItemStack.CODEC);
        if (stack == null || stack.isEmpty()) return icon;
        return new ExtInteractionIcon.StackedIcon(icon_small, new ExtInteractionIcon.ItemStackIcon(stack));
    }
}
