package com.denisjava.extended_interactions.compat;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.*;
import com.denisjava.extended_interactions.config.CommandAction;
import com.denisjava.extended_interactions.config.KeymappingAction;
import com.denisjava.extended_interactions.config.UseItemAction;
import com.denisjava.extended_interactions.impl.EIResultImpl;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon.ItemStackIcon;
import com.denisjava.extended_interactions.util.EIUtils;
import com.denisjava.extended_interactions.util.ThrowableEIResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
//? if <1.21.11 {
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.npc.AbstractVillager;
//?} else {
/*import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
*///? }
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import static com.denisjava.extended_interactions.EICommon.id;
import static com.denisjava.extended_interactions.api.EIResults.*;

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
    public ResourceLocation getUID() {
        return id("builtin");
    }

    // === SHEEP SHEARING ===
    private final ItemOnEntityInteraction SHEEP_SHEARING =
            new ItemOnEntityInteraction(id("mc/sheep_shearing"), new ItemStackIcon(Items.SHEARS), this, Items.SHEARS);
    private EIResultImpl.Result sheepShearing(Level level, Player player, Entity entity) throws ThrowableEIResult {
        if (!((Sheep) entity).readyForShearing()) return failure(SHEEP_SHEARING, "no_wool");
        SHEEP_SHEARING.providerCheck(player);
        return success(SHEEP_SHEARING);
    }

    // === COW MILKING ===
    private final ItemOnEntityInteraction COW_MILKING =
            new ItemOnEntityInteraction(id("mc/cow_milking"), new ItemStackIcon(Items.MILK_BUCKET), this, Items.BUCKET);
    private EIResultImpl.Result cowMilking(Level level, Player player, Entity entity) throws ThrowableEIResult {
        noCreative(COW_MILKING, player); // bucket duplicates
        COW_MILKING.providerCheck(player);
        return success(COW_MILKING);
    }

    // === MOOSHROOM MILKING ===
    private final ItemOnEntityInteraction MOOSHROOM_SOUPING =
            new ItemOnEntityInteraction(id("mc/mooshroom_souping"), new ItemStackIcon(Items.MUSHROOM_STEW), this, Items.BOWL);
    private EIResultImpl.Result mooshroomSouping(Level level, Player player, Entity entity) throws ThrowableEIResult {
        noCreative(MOOSHROOM_SOUPING, player); // bowl duplicates
        MOOSHROOM_SOUPING.providerCheck(player);
        return success(MOOSHROOM_SOUPING);
    }

    // === LEASH ===
    private final LeashInteraction ATTACH_LEASH = new LeashInteraction(id("mc/attach_leash"), new ItemStackIcon(Items.LEAD), this);
    private final UnleashInteraction REMOVE_LEASH = new UnleashInteraction(id("mc/remove_leash"), new ItemStackIcon(Items.LEAD), this);
    private EIResultImpl.Result leashProvider(Level level, Player player, Entity entity) throws ThrowableEIResult {
        if (!(entity instanceof Leashable leashable)) return silentFailure(ATTACH_LEASH);

        if (leashable.isLeashed()) {
            if (leashable.getLeashHolder() instanceof LeashFenceKnotEntity || player.equals(leashable.getLeashHolder())) {
                return success(REMOVE_LEASH);
            }
            return silentFailure(ATTACH_LEASH);
        }
        if (!leashable.canBeLeashed()) return failure(ATTACH_LEASH, "nah");
        if (EIUtils.findItem(player, Items.LEAD) == -1) return failure(ATTACH_LEASH, "no_item");
        return success(ATTACH_LEASH);
    }

    // == SADDLE ==
    private final SaddleInteraction EQUIP_SADDLE = new SaddleInteraction(id("mc/equip_saddle"), new ItemStackIcon(Items.SADDLE), this);
    private EIResultImpl.Result saddleProvider(Level level, Player player, Entity entity) throws ThrowableEIResult {
        //? if <1.21.11 {
        if (!(entity instanceof Saddleable saddleable)) return silentFailure(EQUIP_SADDLE);
        if (saddleable.isSaddled()) return silentFailure(EQUIP_SADDLE);
        if (!saddleable.isSaddleable()) return failure(EQUIP_SADDLE, "nah");
        //? } else {
        /*if (!(entity instanceof Mob mob)) return silentFailure(EQUIP_SADDLE);
        if (mob.isSaddled()) return silentFailure(EQUIP_SADDLE);
        if (!mob.isEquippableInSlot(new ItemStack(Items.SADDLE), EquipmentSlot.SADDLE)) return failure(EQUIP_SADDLE, "nah");
        *///? }
        if (EIUtils.findItem(player, Items.SADDLE) == -1) return failure(EQUIP_SADDLE, "no_item");
        return success(EQUIP_SADDLE);
    }

    // == TRADING ==
    private final VillagerTradeInteraction VILLAGER_TRADING = new VillagerTradeInteraction(id("mc/villager_trade"), new ItemStackIcon(Items.EMERALD), this);
    private EIResultImpl.Result tradingProvider(Level level, Player player, Entity entity) throws ThrowableEIResult {
        if (!level.isClientSide() && ((AbstractVillager) entity).getOffers().isEmpty()) return failure(VILLAGER_TRADING, "no_trades");
        return success(VILLAGER_TRADING);
    }
}
