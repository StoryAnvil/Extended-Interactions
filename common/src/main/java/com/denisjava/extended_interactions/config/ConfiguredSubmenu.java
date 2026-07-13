package com.denisjava.extended_interactions.config;

import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.ItemControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownController;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownControllerElement;
import dev.isxander.yacl3.gui.controllers.dropdown.ItemController;
import dev.isxander.yacl3.gui.controllers.dropdown.ItemControllerElement;
import dev.isxander.yacl3.gui.utils.ItemRegistryHelper;
import dev.isxander.yacl3.gui.utils.MiscUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

import static net.minecraft.network.chat.Component.translatable;

public record ConfiguredSubmenu(String name, Item icon) {
    public ConfiguredSubmenu() {
        this("New submenu", Items.DIRT);
    }

    private static class Configurator {
        private String name;
        private Item icon;

        public Configurator(String name, Item icon) {
            this.name = name;
            this.icon = icon;
        }

        public ConfiguredSubmenu build() {
            return new ConfiguredSubmenu(name, icon);
        }

        public Screen generateConfigScreen(Screen parent, Binding<ConfiguredSubmenu> stateManager) {
            return YetAnotherConfigLib.createBuilder()
                    .title(translatable("extended_interactions.submenu.cfg"))
                    .category(ConfigCategory.createBuilder()
                            .name(translatable("extended_interactions.submenu.cfg"))
                            .tooltip(translatable("extended_interactions.submenu.cfg.tooltip"))
                            .option(Option.<String>createBuilder()
                                    .name(translatable("extended_interactions.submenu.name"))
                                    .description(OptionDescription.of(
                                            translatable("extended_interactions.submenu.name.help")
                                    ))
                                    .binding("Submenu", () -> name, v -> name = v)
                                    .controller(StringControllerBuilder::create)
                                    .build())
                            .option(Option.<Item>createBuilder()
                                    .name(translatable("extended_interactions.submenu.icon"))
                                    .description(OptionDescription.of(
                                            translatable("extended_interactions.submenu.icon.help")
                                    ))
                                    .binding(Items.DIRT, () -> icon, v -> icon = v)
                                    .controller(ItemControllerBuilder::create)
                                    .build())
                            .option(LabelOption.create(translatable("extended_interactions.submenu.save")))
                            .build())
                    .save(() -> stateManager.setValue(build()))
                    .build().generateScreen(parent);
        }
    }

    public static ControllerBuilder<ConfiguredSubmenu> controller(Option<ConfiguredSubmenu> option) {
        return () -> new SubmenuController(option);
    }

    public static class SubmenuController implements Controller<ConfiguredSubmenu> {
        private final Option<ConfiguredSubmenu> option;

        private SubmenuController(Option<ConfiguredSubmenu> option) {
            this.option = option;
        }

        @Override
        public Option<ConfiguredSubmenu> option() {
            return option;
        }

        @Override
        public Component formatValue() {
            return translatable("extended_interactions.submenues.cfg", option.binding().getValue().name);
        }

        @Override
        public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
            return new SubmenuButtonWidget(this, screen, widgetDimension);
        }

        public void open(YACLScreen screen) {
            ConfiguredSubmenu submenu = option.binding().getValue();
            Minecraft.getInstance().setScreen(new Configurator(submenu.name, submenu.icon)
                    .generateConfigScreen(screen, option.binding()));
        }
    }

    public static class SubmenuButtonWidget extends ControllerWidget<SubmenuController> {
        public SubmenuButtonWidget(SubmenuController control, YACLScreen screen, Dimension<Integer> dim) {
            super(control, screen, dim);
        }

        @Override
        protected void drawValueText(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            super.drawValueText(graphics, mouseX, mouseY, delta);

            if (hovered) {
                //? if >=1.21.9
                //graphics.requestCursor(isAvailable() ? com.mojang.blaze3d.platform.cursor.CursorTypes.POINTING_HAND : com.mojang.blaze3d.platform.cursor.CursorTypes.NOT_ALLOWED);
            }
        }

        @Override
        public boolean onMouseClicked(double mouseX, double mouseY, int button) {
            if (isMouseOver(mouseX, mouseY) && isAvailable()) {
                control.open(screen);
                return true;
            }
            return false;
        }

        @Override
        public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
            if (!focused) {
                return false;
            }

            if (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_SPACE || keyCode == InputConstants.KEY_NUMPADENTER) {
                control.open(screen);
                return true;
            }

            return false;
        }

        @Override
        protected int getHoveredControlWidth() {
            return getUnhoveredControlWidth();
        }

        @Override
        public boolean canReset() {
            return false;
        }
    }

    public static ControllerBuilder<String> selectorController(Option<String> option) {
        return () -> new SubmenuSelectorController(option);
    }

    public static class SubmenuSelectorController extends AbstractDropdownController<String> {
        public SubmenuSelectorController(Option<String> option) {
            super(option);
        }

        @Override
        public String getString() {
            return option.pendingValue();
        }

        @Override
        public void setFromString(String value) {
            option.requestSet(value);
        }

        @Override
        public Component formatValue() {
            return Component.literal(getString());
        }


        @Override
        public boolean isValueValid(String value) {
            for (ConfiguredSubmenu submenu : EIClientConfig.SUBMENUES.get().pendingValue()) {
                if (submenu.name.equals(value)) return true;
            }
            return false;
        }

        @Override
        protected String getValidValue(String value, int offset) {
            if (offset == -1) return getString();
            String q = value.toLowerCase();
            return EIClientConfig.SUBMENUES.get().pendingValue().stream().map(ConfiguredSubmenu::name)
                    .filter(s -> s.toLowerCase().contains(q)).skip(offset).findFirst().orElseGet(this::getString);
        }

        @Override
        public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
            return new SubmenuSelectorControllerElement(this, screen, widgetDimension);
        }
    }

    public static class SubmenuSelectorControllerElement extends AbstractDropdownControllerElement<String, String> {
        private final SubmenuSelectorController submenuController;
        private ConfiguredSubmenu currentSubmenu = null;

        public SubmenuSelectorControllerElement(SubmenuSelectorController control, YACLScreen screen, Dimension<Integer> dim) {
            super(control, screen, dim);
            this.submenuController = control;
        }

        @Override
        protected void drawValueText(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            var oldDimension = getDimension();
            setDimension(getDimension().withWidth(getDimension().width() - getDecorationPadding()));
            super.drawValueText(graphics, mouseX, mouseY, delta);
            setDimension(oldDimension);
            if (currentSubmenu != null) {
                graphics.renderFakeItem(new ItemStack(currentSubmenu.icon), getDimension().xLimit() - getXPadding() - getDecorationPadding() + 2, getDimension().y() + 2);
            }
        }

        @Override
        public List<String> computeMatchingValues() {
            String q = inputField.toLowerCase();
            return EIClientConfig.SUBMENUES.get().pendingValue().stream().filter(s -> s.name.toLowerCase().contains(q))
                    .map(ConfiguredSubmenu::name).toList();
        }

        @Override
        protected void renderDropdownEntry(GuiGraphics graphics, Dimension<Integer> entryDimension, String id) {
            super.renderDropdownEntry(graphics, entryDimension, id);
            ConfiguredSubmenu submenu = EIClientConfig.HANDLER.instance().getPendingSubmenuByName(id);
            graphics.renderFakeItem(
                    new ItemStack(submenu != null ? submenu.icon : Items.BARRIER),
                    entryDimension.xLimit() - 2,
                    entryDimension.y() + 1
            );
        }

        @Override
        public String getString(String id) {
            ConfiguredSubmenu submenu = EIClientConfig.HANDLER.instance().getPendingSubmenuByName(id);
            return submenu == null ? "Deleted submenu" : submenu.name;
        }

        @Override
        protected int getDecorationPadding() {
            return 16;
        }

        @Override
        protected int getDropdownEntryPadding() {
            return 4;
        }

        @Override
        protected int getControlWidth() {
            return super.getControlWidth() + getDecorationPadding();
        }

        @Override
        protected Component getValueText() {
            if (inputField.isEmpty() || submenuController == null)
                return super.getValueText();

            if (inputFieldFocused)
                return Component.literal(inputField);

            return Component.literal(submenuController.option().pendingValue());
        }
    }
}
