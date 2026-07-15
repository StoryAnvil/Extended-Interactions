package com.denisjava.extended_interactions.client;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.providers.EIResult;
import com.denisjava.extended_interactions.api.providers.FailedResult;
import com.denisjava.extended_interactions.api.providers.SuccessfulResult;
import com.denisjava.extended_interactions.config.ConfiguredSubmenu;
import com.denisjava.extended_interactions.config.EIClientConfig;
import com.denisjava.extended_interactions.config.ExtInteractionState;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import com.denisjava.extended_interactions.impl.MenuTarget;
import com.denisjava.extended_interactions.impl.RadialMenuButton;
import com.denisjava.extended_interactions.util.EIUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Stores radial menu buttons and error messages.<br>
 * Does not handle rendering
 */
public class RadialMenuData {
    protected List<RadialMenuButton> buttons;

    private RadialMenuData(List<RadialMenuButton> buttons) {
        this.buttons = buttons;
    }

    public @UnmodifiableView List<RadialMenuButton> getButtons() {
        return buttons;
    }

    /**
     * Takes list of buttons and puts them in categories.<br>
     * Categories are represented by {@link CategoryData} instances in returned list
     */
    private static List<RadialMenuButton> categorize(List<RadialMenuButton> uncategorized) {
        ArrayList<RadialMenuButton> categorized = new ArrayList<>(uncategorized.size());

        l0: for (RadialMenuButton button : uncategorized) {
            String category = getCategory(button);

            if (category == null) {
                // Add item directly if it does not have a category
                categorized.add(button);
                continue;
            }

            // Try to find existing category data
            for (RadialMenuButton btn : categorized) {
                if (category.equals(btn._categoryNameHook())) {
                    ((CategoryData) btn).buttons.add(button);
                    continue l0;
                }
            }
            // If category was not found, create it
            ConfiguredSubmenu configuredSubmenu = EIClientConfig.HANDLER.instance().getSubmenuByName(category);
            CategoryData data = new CategoryData(configuredSubmenu.name(), new ExtInteractionIcon.ItemStackIcon(configuredSubmenu.icon()));
            data.buttons.add(button);
            categorized.add(data);
        }

        // Iff categorized buttons consists of only one category, expand this category
        if (EIClientConfig.HANDLER.instance().dontCollapseToSingleCategory && categorized.size() == 1 && categorized.getFirst() instanceof CategoryData) {
            return uncategorized;
        }

        // Find all categories that only have one button in them and expand them
        if (EIClientConfig.HANDLER.instance().expandSingleItemCategories) {
            for (int i = 0; i < categorized.size(); i++) {
                // Size of category with only one button is 2 because of "Back" button
                if (categorized.get(i) instanceof CategoryData categoryData && categoryData.buttons.size() == 2) {
                    categorized.set(i, categoryData.buttons.get(1));
                }
            }
        }

        return categorized;
    }

    private static String getCategory(RadialMenuButton button) {
        if (button instanceof EIResult r) {
            String id = r.getInteraction().getId().toString();
            String category = EIClientConfig.HANDLER.instance().submenuBinds.get(id);
            if (category != null && EIClientConfig.HANDLER.instance().getSubmenuByName(category) != null)
                return category;
        }
        return null;
    }

    public static class CategoryData extends RadialMenuData implements RadialMenuButton, ClientRadialMenuButton {
        private final String name;
        private final ExtInteractionIcon icon;
        private RadialMenuData parent = null;
        private CategoryData(String name, ExtInteractionIcon icon) {
            super(new ArrayList<>());
            buttons.add(new CategoryReturnButton(this));
            this.name = name;
            this.icon = icon;
        }

        @Override
        public Component getName() {
            return Component.literal(name);
        }

        @Override
        public ExtInteractionIcon getIcon() {
            return icon;
        }

        @Override
        public boolean isClientSide() {
            return true;
        }

        @Override
        public void executeClientSide(RadialMenuScreen screen) {
            parent = screen.data;
            screen.data = this;
            screen.onDataUpdate();
        }

        @Override
        public Identifier getId() {
            return Identifier.fromNamespaceAndPath("category", name);
        }

        @Override
        public @Nullable String _categoryNameHook() {
            return name;
        }
    }
    private record CategoryReturnButton(CategoryData owner) implements RadialMenuButton, ClientRadialMenuButton {
        @Override
        public void executeClientSide(RadialMenuScreen screen) {
            screen.data = owner.parent;
            owner.parent = null;
            screen.onDataUpdate();
        }

        @Override
        public Component getName() {
            return Component.translatable("gui.extended_interactions.back");
        }

        @Override
        public ExtInteractionIcon getIcon() {
            return new ExtInteractionIcon.ItemStackIcon(Items.PINK_GLAZED_TERRACOTTA);
        }

        @Override
        public Identifier getId() {
            return EICommon.id("back");
        }
    }

    // === FACTORIES
    public static RadialMenuData createEmpty() {
        return new RadialMenuData(List.of());
    }

    public static Pair<RadialMenuData, List<RadialMenuButton>> createPrediction(MenuTarget target) {
        Pair<List<SuccessfulResult>, List<FailedResult>> result =
                target.collectClientSide(Minecraft.getInstance().player);

        EIClientConfig config = EIClientConfig.HANDLER.instance();
        List<RadialMenuButton> original = result.getFirst().stream()
                .filter(r -> config.interactions.get(r.getInteraction().getId().toString()) != ExtInteractionState.HIDE)
                .map(r -> (RadialMenuButton) r).toList();
        return new Pair<>(
                new RadialMenuData(categorize(original)),
                original
        );
    }

    public static RadialMenuData createMerged(List<RadialMenuButton> original, List<SuccessfulResult> good, List<FailedResult> bad) {
        EIClientConfig config = EIClientConfig.HANDLER.instance();

        // Order merged buttons as predicated buttons were
        original = categorize(EIUtils.preservedSort(RadialMenuButton::getId, original,
                        // Merge client-side buttons and buttons from server
                        Stream.concat(original.stream().filter(RadialMenuButton::isClientSide), good.stream()
                                .filter(r -> config.interactions.get(r.getInteraction().getId().toString()) != ExtInteractionState.HIDE)))
                .toList());
        return new RadialMenuData(original);
    }
}
