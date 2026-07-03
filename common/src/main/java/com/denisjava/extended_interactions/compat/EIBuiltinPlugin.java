package com.denisjava.extended_interactions.compat;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.*;
import com.denisjava.extended_interactions.config.CommandAction;
import com.denisjava.extended_interactions.config.KeymappingAction;
import com.denisjava.extended_interactions.impl.EIResultImpl;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon.ItemStackIcon;
import com.denisjava.extended_interactions.util.ThrowableEIResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
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

        registrar.register(SHEEP_SHEARING);
        registrar.register(COW_MILKING);
        registrar.register(MOOSHROOM_SOUPING);
        registrar.register(ATTACH_LEASH);
    }

    @Override
    public void registerProviders(ProviderRegistrar registrar) {
        registrar.entityProvider(EntityType.SHEEP, this::sheepShearing);
        registrar.entityProvider(EntityType.COW, this::cowMilking);
        registrar.entityProvider(EntityType.MOOSHROOM, this::cowMilking);
        registrar.entityProvider(EntityType.MOOSHROOM, this::mooshroomSouping);
        registrar.universalEntityProvider(this::attachLeash);
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
        check(SHEEP_SHEARING, player, "no_item");
        return success(SHEEP_SHEARING);
    }

    // === COW MILKING ===
    private final ItemOnEntityInteraction COW_MILKING =
            new ItemOnEntityInteraction(id("mc/cow_milking"), new ItemStackIcon(Items.MILK_BUCKET), this, Items.BUCKET);
    private EIResultImpl.Result cowMilking(Level level, Player player, Entity entity) throws ThrowableEIResult {
        noCreative(COW_MILKING, player); // bucket duplicates
        check(COW_MILKING, player, "no_item");
        return success(COW_MILKING);
    }

    // === MOOSHROOM MILKING ===
    private final ItemOnEntityInteraction MOOSHROOM_SOUPING =
            new ItemOnEntityInteraction(id("mc/mooshroom_souping"), new ItemStackIcon(Items.MUSHROOM_STEW), this, Items.BOWL);
    private EIResultImpl.Result mooshroomSouping(Level level, Player player, Entity entity) throws ThrowableEIResult {
        noCreative(MOOSHROOM_SOUPING, player); // bowl duplicates
        check(MOOSHROOM_SOUPING, player, "no_item");
        return success(MOOSHROOM_SOUPING);
    }

    // === LEASH ===
    private final ItemOnEntityInteraction ATTACH_LEASH =
            new ItemOnEntityInteraction(id("mc/attach_leash"), new ItemStackIcon(Items.LEAD), this, Items.LEAD);
    private EIResultImpl.Result attachLeash(Level level, Player player, Entity entity) throws ThrowableEIResult {
        if (!(entity instanceof Leashable leashable)) return silentFailure(ATTACH_LEASH);
        if (leashable.isLeashed()) return failure(ATTACH_LEASH, "leashed");
        if (!leashable.canBeLeashed()) return failure(ATTACH_LEASH, "nah");
        check(ATTACH_LEASH, player, "no_item");
        return success(ATTACH_LEASH);
    }
}
