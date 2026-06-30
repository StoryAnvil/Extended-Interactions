package com.denisjava.extended_interactions;

import com.denisjava.extended_interactions.api.EIResults;
import com.denisjava.extended_interactions.impl.EIResultImpl;
import com.denisjava.extended_interactions.impl.ExtendedInteractionsImpl;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
//? if >=1.21.11
//import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.Objects;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class EICommands {
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(literal("extended_interactions")
                //? if >=1.21.11 {
                /*.requires(css -> css.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                *///?} else
                .requires(css -> css.hasPermission(2))

                .then(literal("listResults")
                        .then(literal("block").then(argument("pos", BlockPosArgument.blockPos()).executes(EICommands::listBlock)))
                        .then(literal("entity").then(argument("subject", EntityArgument.entity()).executes(EICommands::listEntities)))
                )
        );
    }

    private static int listBlock(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerLevel level = ctx.getSource().getLevel();
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
        listResults(ctx, ExtendedInteractionsImpl.collectForBlock(level, ctx.getSource().getPlayerOrException(), pos));
        return 0;
    }

    private static int listEntities(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerLevel level = ctx.getSource().getLevel();
        Entity entity = EntityArgument.getEntity(ctx, "subject");
        listResults(ctx, ExtendedInteractionsImpl.collectForEntity(level, ctx.getSource().getPlayerOrException(), entity));
        return 0;
    }

    private static void listResults(CommandContext<CommandSourceStack> ctx, Collection<EIResultImpl.Result> results) {
        if (results.isEmpty()) {
            ctx.getSource().sendSystemMessage(Component.translatable("debug.extended_interactions.no_results"));
            return;
        }
        ctx.getSource().sendSystemMessage(Component.translatable("debug.extended_interactions.results", results.size()));
        for (EIResultImpl.Result result : results) {
            MutableComponent component = null;
            if (result instanceof EIResultImpl.Successful success) {
                component = Component.translatable("debug.extended_interactions.success", success.interaction.getId().toString());
            }
            if (result instanceof EIResultImpl.SilentlyFailed sf) {
                component = Component.translatable("debug.extended_interactions.silent_failure", sf.interaction.getId().toString());
            }
            if (result instanceof EIResultImpl.Failed fail) {
                component = Component.translatable("debug.extended_interactions.failure", fail.interaction.getId().toString(), fail.error);
            }
            ctx.getSource().sendSystemMessage(Objects.requireNonNull(component, "bad result type"));
        }
    }
}
