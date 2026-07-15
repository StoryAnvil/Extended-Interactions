package com.denisjava.extended_interactions.api;

import com.denisjava.extended_interactions.api.providers.EIResult;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import com.denisjava.extended_interactions.util.EIPlayer;
import com.denisjava.extended_interactions.util.EIUtils;
import com.denisjava.extended_interactions.util.ThrowableEIResult;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public class ItemOnEntityInteraction extends JavaInteraction implements ExtInteraction.SimpleProvider {
    private final Predicate<ItemStack> item;
    public ItemOnEntityInteraction(Identifier id, ExtInteractionIcon icon, EIPlugin declaringPlugin, Predicate<ItemStack> item) {
        super(id, icon, declaringPlugin);
        this.item = item;
    }

    public ItemOnEntityInteraction(Identifier id, ExtInteractionIcon icon, EIPlugin declaringPlugin, Item item) {
        this(id, icon, declaringPlugin, stack -> stack.is(item));
    }

    @Override
    public void handleEntityExecution(Player player, Level level, Entity target) {
        int slot = EIUtils.findItem(player, item);
        if (slot == -1) return;
        try {
            ((EIPlayer) player).ei$overrideMainHandSlot(slot);
            player.interactOn(target, InteractionHand.MAIN_HAND);
        } finally {
            ((EIPlayer) player).ei$overrideMainHandSlot();
        }
    }

    @Override
    public void providerCheck(Player player) throws ThrowableEIResult {
        if (EIUtils.findItem(player, item) == -1) {
            EIResult.fail(this).addReason("no_item").throwNow();
        }
    }
}
