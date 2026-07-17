package com.denisjava.extended_interactions.compat;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.*;
import com.denisjava.extended_interactions.api.providers.EIResult;
import com.denisjava.extended_interactions.api.providers.EIResultCollector;
import com.denisjava.extended_interactions.config.CommandAction;
import com.denisjava.extended_interactions.config.KeymappingAction;
import com.denisjava.extended_interactions.config.UseItemAction;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon.ComponentIcon;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon.ItemStackIcon;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon.TexturedSpriteIcon;
import com.denisjava.extended_interactions.util.EIUtils;
import com.denisjava.extended_interactions.util.ThrowableEIResult;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
//? if <1.21.11 {
import net.minecraft.world.entity.animal.Sheep;
//?} else {
/*import net.minecraft.world.entity.animal.sheep.Sheep;
*///? }
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import static com.denisjava.extended_interactions.EICommon.id;

@EIPluginClass
public class EIBuiltinPlugin implements EIPlugin {
    @Override
    public void registerInteractions(@NotNull InteractionRegistrar registrar) {
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
        registrar.register(DELAY);
        registrar.register(TAKE_RESULT);
        //registrar.register(NOTE);
    }

    @Override
    public void registerProviders(@NotNull ProviderRegistrar registrar) {
        registrar.entityProvider(EntityType.SHEEP, this::sheepShearing);
        registrar.entityProvider(EntityType.COW, this::cowMilking);
        registrar.entityProvider(EntityType.MOOSHROOM, this::cowMilking);
        registrar.entityProvider(EntityType.MOOSHROOM, this::mooshroomSouping);
        registrar.entityProvider(EntityType.VILLAGER, VILLAGER_TRADING);
        registrar.entityProvider(EntityType.WANDERING_TRADER, VILLAGER_TRADING);

        registrar.blockProvider(Blocks.REPEATER, DELAY::blockProvider);
        registrar.blockTagProvider(EIUtils.blocks("container"), TAKE_RESULT);
        //registrar.blockProvider(Blocks.NOTE_BLOCK, NOTE::blockProvider);

        registrar.universalEntityProvider(this::leashProvider);
        registrar.universalEntityProvider(EQUIP_SADDLE);
    }

    @Override
    public @NotNull String getDeclaringModId() {
        return EICommon.MOD_ID;
    }

    @Override
    public @NotNull ResourceLocation getUID() {
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

    // == TRADING ==
    private final VillagerTradeInteraction VILLAGER_TRADING = new VillagerTradeInteraction(id("mc/villager_trade"), new ItemStackIcon(Items.EMERALD), this);

    // == CONTAINERS ==
    private final TakeResultInteraction TAKE_RESULT = new TakeResultInteraction(id("mc/take"), this);

    // == CONFIGURATORS ==
    private final StateConfiguratorInteraction<Integer> DELAY = new StateConfiguratorInteraction.WithIcons<>(id("mc/delay_cfg"), new ItemStackIcon(Items.COMPARATOR),
            BlockStateProperties.DELAY, this, i -> switch (i) {
        case 1 -> new ComponentIcon("1", 0xFF0000);
        case 2 -> new ComponentIcon("2", 0xFF2700);
        case 3 -> new ComponentIcon("3", 0xFF9B00);
        case 4 -> new ComponentIcon("4", 0xFFB00);
        default -> throw new AssertionError("Bad property");
    });
    /*private final StateConfiguratorInteraction<Integer> NOTE = new StateConfiguratorInteraction.WithIcons<>(id("mc/note_cfg"), new ItemStackIcon(Items.JUKEBOX),
            BlockStateProperties.NOTE, this, i -> switch (i) {
        case 0, 24 -> new ComponentIcon("F♯", 0x59E800);
        case 1 -> new ComponentIcon("G", 0x82CE00);
        case 2 -> new ComponentIcon("G♯", 0xACA00);
        case 3 -> new ComponentIcon("A", 0xCE8400);
        case 4 -> new ComponentIcon("A♯", 0xE85900);
        case 5 -> new ComponentIcon("B", 0xF92E00);
        case 6 -> new ComponentIcon("C", 0xFF0606);
        case 7 -> new ComponentIcon("C♯", 0xF9002E);
        case 8 -> new ComponentIcon("D", 0xE80059);
        case 9 -> new ComponentIcon("D♯", 0xCE0082);
        case 10 -> new ComponentIcon("E", 0xAC00AC);
        case 11 -> new ComponentIcon("F", 0x8200CE);
        case 12 -> new ComponentIcon("F♯", 0x5900E8);
        case 13 -> new ComponentIcon("G", 0x2E00F9);
        case 14 -> new ComponentIcon("G♯", 0x0606FF);
        case 15 -> new ComponentIcon("A", 0x002EF9);
        case 16 -> new ComponentIcon("A♯", 0x0059E8);
        case 17 -> new ComponentIcon("B", 0x0082CE);
        case 18 -> new ComponentIcon("C", 0x00ACAC);
        case 19 -> new ComponentIcon("C♯", 0x00CE82);
        case 20 -> new ComponentIcon("D", 0x00E859);
        case 21 -> new ComponentIcon("D♯", 0x00F9E);
        case 22 -> new ComponentIcon("E", 0x06FF06);
        case 23 -> new ComponentIcon("F", 0x2EF900);
        default -> throw new AssertionError("Bad property");
    });*/
}
