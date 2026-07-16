//? if patchouli {
package com.denisjava.extended_interactions.compat.patchouli;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.InteractionArgument;
import com.denisjava.extended_interactions.api.JavaInteraction;
import com.denisjava.extended_interactions.api.providers.EIBlockProvider;
import com.denisjava.extended_interactions.api.providers.EIResult;
import com.denisjava.extended_interactions.api.providers.EIResultCollector;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import com.denisjava.extended_interactions.util.ThrowableEIResult;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class PatchouliLookupInteraction extends JavaInteraction implements EIBlockProvider {
    public PatchouliLookupInteraction(ResourceLocation id, ExtInteractionIcon icon, EIPlugin declaringPlugin) {
        super(id, icon, declaringPlugin);
    }

    @Override
    public ExtInteractionIcon getIcon(String overrideName) {
        ResourceLocation key = ResourceLocation.tryParse(overrideName);
        if (key == null) {
            EICommon.LOG.info("Failed to get Patchouli book icon: {} is not a valid book id", overrideName);
            return ExtInteractionIcon.ERROR_ICON;
        }
        Book book = BookRegistry.INSTANCE.books.get(key);
        if (book == null) {
            EICommon.LOG.info("Failed to get Patchouli book icon: {} book does not exist", key);
            return ExtInteractionIcon.ERROR_ICON;
        }
        if (book.getIcon() != null)
            return new PatchouliBookIcon(book.getIcon());
        EICommon.LOG.info("Failed to get Patchouli book icon: {} book does not have a icon. Falling back to ItemStackIcon", key);
        return new ExtInteractionIcon.ItemStackIcon(book.getBookItem());
    }

    @Override
    public void handleBlockExecution(Player player, Level level, BlockPos pos, BlockState state, String argumentId) {
        if (level.isClientSide()) {
            ItemStack stack = new ItemStack(state.getBlock().asItem());
            for (Book book : collectBooks(player)) {
                if (book.id.toString().equals(argumentId)) {
                    Pair<BookEntry, Integer> entry = book.getContents().getEntryForStack(stack);
                    if (entry != null && !entry.getFirst().isLocked()) {
                        PatchouliAPI.get().openBookEntry(book.id, entry.getFirst().getId(), entry.getSecond());
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void collectForBlock(EIResultCollector collector, Level level, Player player, BlockPos pos, BlockState state) throws ThrowableEIResult {
        ItemStack stack = new ItemStack(state.getBlock().asItem());
        HashSet<ResourceLocation> books = new HashSet<>();
        List<InteractionArgument> arguments = new ArrayList<>(0);
        for (Book book : collectBooks(player)) {
            if (books.contains(book.id)) continue;
            books.add(book.id);
            Pair<BookEntry, Integer> entry = book.getContents().getEntryForStack(stack);
            if (entry != null && !entry.getFirst().isLocked()) {
                String id = book.id.toString();
                arguments.add(new InteractionArgument(id,
                        Optional.of(Component.translatable("extinter.extended_interactions.patchouli/lookup1", book.getBookItem().getDisplayName())),
                        Optional.of(id)));
            }
        }
        if (!arguments.isEmpty()) {
            collector.add(EIResult.success(this)
                    .addArguments(arguments));
        }
    }

    private List<Book> collectBooks(Player player) {
        ArrayList<Book> books = new ArrayList<>();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            Book book = ItemStackUtil.getBookFromStack(stack);
            if (book != null) books.add(book);
        }
        return books;
    }
}
//? }