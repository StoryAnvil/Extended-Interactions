package com.denisjava.extended_interactions.config;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.impl.MenuTarget;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public record CommandAction(String command) implements DataDrivenAction {
    public static final MapCodec<CommandAction> CODEC =
            Codec.STRING.fieldOf("cmd").xmap(CommandAction::new, CommandAction::command);

    @Override
    public void handle(Player player, MenuTarget target) {
        if (player.level() instanceof ServerLevel level) {
            String tag = "ei" + level.getRandomSequence(EICommon.id("tags")).nextIntBetweenInclusive(1000000, 9999999) + "P";
            player.getTags().add(tag);
            level.getServer().getCommands().performPrefixedCommand(target.createStack(level),
                    command.replace("@ei:user", "@e[limit=1,type=player,tag=" + tag + ']'));
            player.getTags().remove(tag);
            //EICommon.LOG.info("Cmd: {}", command.replace("@ei:user", "@e[limit=1,type=player,tag=" + tag + ']'));
        }
    }

    @Override
    public Identifier getId() {
        return EICommon.id("command");
    }
}
