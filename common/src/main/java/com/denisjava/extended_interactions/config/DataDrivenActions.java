package com.denisjava.extended_interactions.config;

import com.denisjava.extended_interactions.client.EIClient;
import com.denisjava.extended_interactions.impl.ExtendedInteractionsImpl;
import com.denisjava.extended_interactions.impl.MenuTarget;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import static com.denisjava.extended_interactions.impl.ExtendedInteractionsImpl.registerAction;

public class DataDrivenActions {
    public static final Codec<DataDrivenAction> CODEC = ResourceLocation.CODEC.dispatch(DataDrivenAction::getId, ExtendedInteractionsImpl::getActionCodec);

    public static void register() {
        registerAction(sid("command"), CommandAction.CODEC);
        registerAction(sid("keymapping"), KeymappingAction.CODEC);
    }

    public record CommandAction(String command) implements DataDrivenAction {
        public static final MapCodec<CommandAction> CODEC =
                Codec.STRING.fieldOf("cmd").xmap(CommandAction::new, CommandAction::command);

        @Override
        public void handle(Player player, MenuTarget target) {
            if (player.level() instanceof ServerLevel level) {
                String tag = "ei" + level.getRandomSequence(sid("tags")).nextIntBetweenInclusive(1000000, 9999999) + "P";
                player.getTags().add(tag);
                level.getServer().getCommands().performPrefixedCommand(target.createStack(level),
                        command.replace("@ei:user", "@e[limit=1,type=player,tag=" + tag + ']'));
                player.getTags().remove(tag);
                //EICommon.LOG.info("Cmd: {}", command.replace("@ei:user", "@e[limit=1,type=player,tag=" + tag + ']'));
            }
        }

        @Override
        public ResourceLocation getId() {
            return sid("command");
        }
    }

    public record KeymappingAction(String keymapping) implements DataDrivenAction {
        public static final MapCodec<KeymappingAction> CODEC =
                Codec.STRING.fieldOf("key").xmap(KeymappingAction::new, KeymappingAction::keymapping);

        @Override
        public void handle(Player player, MenuTarget target) {
            if (player.level().isClientSide()) EIClient.pressKey(keymapping);
        }

        @Override
        public ResourceLocation getId() {
            return sid("keymapping");
        }
    }

    public static ResourceLocation sid(String path) {
        return ResourceLocation.fromNamespaceAndPath("ei", path);
    }
}
