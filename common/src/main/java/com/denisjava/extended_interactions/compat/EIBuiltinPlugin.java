package com.denisjava.extended_interactions.compat;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.*;
import com.denisjava.extended_interactions.api.providers.EIResult;
import com.denisjava.extended_interactions.api.providers.EIResultCollector;
import com.denisjava.extended_interactions.config.CommandAction;
import com.denisjava.extended_interactions.config.KeymappingAction;
import com.denisjava.extended_interactions.config.UseItemAction;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon.ItemStackIcon;
import com.denisjava.extended_interactions.util.EIUtils;
import com.denisjava.extended_interactions.util.ThrowableEIResult;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.*;
//? if <1.21.11 {
/*import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.npc.AbstractVillager;
*///?} else {
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
//? }
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import static com.denisjava.extended_interactions.EICommon.id;

@EIPluginClass
public class EIBuiltinPlugin implements EIPlugin {
    @Override
    public void registerInteractions(InteractionRegistrar registrar) {
        registrar.registerDataDrivenAction(id("command"), CommandAction.CODEC);
        registrar.registerDataDrivenAction(id("key"), KeymappingAction.CODEC);
        registrar.registerDataDrivenAction(id("use_item"), UseItemAction.CODEC);

        registrar.register(SHEEP_SHEARING);
        registrar.register(COW_MILKING);
        registrar.register(MOOSHROOM_SOUPING);
        registrar.register(ATTACH_LEASH);
        registrar.register(REMOVE_LEASH);
        registrar.register(EQUIP_SADDLE);
        registrar.register(VILLAGER_TRADING);
    }

    @Override
    public void registerProviders(ProviderRegistrar registrar) {
        registrar.entityProvider(EntityType.SHEEP, this::sheepShearing);
        registrar.entityProvider(EntityType.COW, this::cowMilking);
        registrar.entityProvider(EntityType.MOOSHROOM, this::cowMilking);
        registrar.entityProvider(EntityType.MOOSHROOM, this::mooshroomSouping);
        registrar.universalEntityProvider(this::leashProvider);
        registrar.universalEntityProvider(this::saddleProvider);
        registrar.entityProvider(EntityType.VILLAGER, this::tradingProvider);
        registrar.entityProvider(EntityType.WANDERING_TRADER, this::tradingProvider);
    }

    @Override
    public String getDeclaringModId() {
        return EICommon.MOD_ID;
    }

    @Override
    public Identifier getUID() {
        return id("builtin");
    }

    private void noCreative(ExtInteraction interaction, Player player) throws ThrowableEIResult {
        if (player.hasInfiniteMaterials())
            EIResult.fail(interaction).addReason(Component.translatable("extinter.generic.no_creative"), "no_creative").throwNow();
    }

    // === SHEEP SHEARING ===
    private final ItemOnEntityInteraction SHEEP_SHEARING =
            new ItemOnEntityInteraction(id("mc/sheep_shearing"), new ItemStackIcon(Items.SHEARS), this, Items.SHEARS);
    private void sheepShearing(EIResultCollector collector, Level level, Player player, Entity entity) throws ThrowableEIResult {
        if (!((Sheep) entity).readyForShearing()) {
            collector.add(EIResult.fail(SHEEP_SHEARING).addReason("no_wool"));
            return;
        }
        SHEEP_SHEARING.providerCheck(player);
        collector.add(EIResult.success(SHEEP_SHEARING));
    }

    // === COW MILKING ===
    private final ItemOnEntityInteraction COW_MILKING =
            new ItemOnEntityInteraction(id("mc/cow_milking"), new ItemStackIcon(Items.MILK_BUCKET), this, Items.BUCKET);
    private void cowMilking(EIResultCollector collector, Level level, Player player, Entity entity) throws ThrowableEIResult {
        noCreative(COW_MILKING, player); // bucket duplicates
        COW_MILKING.providerCheck(player);
        collector.add(EIResult.success(COW_MILKING));
    }

    // === MOOSHROOM MILKING ===
    private final ItemOnEntityInteraction MOOSHROOM_SOUPING =
            new ItemOnEntityInteraction(id("mc/mooshroom_souping"), new ItemStackIcon(Items.MUSHROOM_STEW), this, Items.BOWL);
    private void mooshroomSouping(EIResultCollector collector, Level level, Player player, Entity entity) throws ThrowableEIResult {
        noCreative(MOOSHROOM_SOUPING, player); // bowl duplicates
        MOOSHROOM_SOUPING.providerCheck(player);
        collector.add(EIResult.success(MOOSHROOM_SOUPING));
    }

    // === LEASH ===
    private final LeashInteraction ATTACH_LEASH = new LeashInteraction(id("mc/attach_leash"), new ItemStackIcon(Items.LEAD), this);
    private final UnleashInteraction REMOVE_LEASH = new UnleashInteraction(id("mc/remove_leash"), new ItemStackIcon(Items.LEAD), this);
    private void leashProvider(EIResultCollector collector, Level level, Player player, Entity entity) throws ThrowableEIResult {
        if (!(entity instanceof Leashable leashable)) {
            collector.add(EIResult.fail(ATTACH_LEASH));
            return;
        }

        if (leashable.isLeashed()) {
            if (leashable.getLeashHolder() instanceof LeashFenceKnotEntity || player.equals(leashable.getLeashHolder())) {
                collector.add(EIResult.success(REMOVE_LEASH));
                return;
            }
            collector.add(EIResult.fail(ATTACH_LEASH));
            return;
        }
        if (!leashable.canBeLeashed()) {
            collector.add(EIResult.fail(ATTACH_LEASH).addReason("nah"));
            return;
        }
        if (EIUtils.findItem(player, Items.LEAD) == -1) {
            collector.add(EIResult.fail(ATTACH_LEASH).addReason("no_item"));
            return;
        }
        collector.add(EIResult.success(ATTACH_LEASH));
    }

    // == SADDLE ==
    private final SaddleInteraction EQUIP_SADDLE = new SaddleInteraction(id("mc/equip_saddle"), new ItemStackIcon(Items.SADDLE), this);
    private void saddleProvider(EIResultCollector collector, Level level, Player player, Entity entity) throws ThrowableEIResult {
        //? if <1.21.11 {
        /*if (!(entity instanceof Saddleable saddleable)) {
            collector.add(EIResult.fail(EQUIP_SADDLE));
            return;
        }
        if (saddleable.isSaddled()) {
            collector.add(EIResult.fail(EQUIP_SADDLE));
            return;
        }
        if (!saddleable.isSaddleable()) {
            collector.add(EIResult.fail(EQUIP_SADDLE).addReason("nah"));
            return;
        }
        *///? } else {
        if (!(entity instanceof Mob mob)) {
            collector.add(EIResult.fail(EQUIP_SADDLE));
            return;
        }
        if (mob.isSaddled()) {
            collector.add(EIResult.fail(EQUIP_SADDLE));
            return;
        }
        if (!mob.isEquippableInSlot(new ItemStack(Items.SADDLE), EquipmentSlot.SADDLE)) {
            collector.add(EIResult.fail(EQUIP_SADDLE).addReason("nah"));
            return;
        }
        //? }
        if (EIUtils.findItem(player, Items.SADDLE) == -1) {
            collector.add(EIResult.fail(EQUIP_SADDLE).addReason("no_item"));
            return;
        }
        collector.add(EIResult.success(EQUIP_SADDLE));
    }

    // == TRADING ==
    private final VillagerTradeInteraction VILLAGER_TRADING = new VillagerTradeInteraction(id("mc/villager_trade"), new ItemStackIcon(Items.EMERALD), this);
    private void tradingProvider(EIResultCollector collector, Level level, Player player, Entity entity) throws ThrowableEIResult {
        if (!level.isClientSide() && ((AbstractVillager) entity).getOffers().isEmpty()) {
            collector.add(EIResult.fail(VILLAGER_TRADING).addReason("no_trades"));
            return;
        }
        collector.add(EIResult.success(VILLAGER_TRADING));
    }
}
