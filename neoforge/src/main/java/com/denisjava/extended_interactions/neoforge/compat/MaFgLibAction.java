package com.denisjava.extended_interactions.neoforge.compat;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.config.DataDrivenAction;
import com.denisjava.extended_interactions.impl.MenuTarget;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindCategory;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public class MaFgLibAction implements DataDrivenAction {
    public static final MapCodec<MaFgLibAction> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.STRING.fieldOf("category").forGetter(MaFgLibAction::getCategory),
            Codec.STRING.fieldOf("keybind").forGetter(MaFgLibAction::getKeybind)
    ).apply(inst, MaFgLibAction::new));

    public final String category;
    public final String keybind;
    private IHotkey cachedKey = null;

    public MaFgLibAction(String category, String keybind) {
        this.category = category;
        this.keybind = keybind;
    }

    @Override
    public void handle(Player player, MenuTarget target) {
        if (!player.level().isClientSide()) return;
        if (getCachedKey().getKeybind() instanceof KeybindMulti multi) {
            if (multi.getCallback() != null) {
                multi.getCallback().onKeyAction(KeyAction.BOTH, multi);
            }
        } else {
            EICommon.LOG.warn("Failed to execute MaFgLib hotkey {} from {} category. Is is not instance of KeybindMulti or it does not exist", keybind, category);
            EICommon.LOG.warn("Currently possessing keybind {} (instance of {})", getCachedKey(), getCachedKey().getClass().getCanonicalName());
        }
    }

    @Override
    public Identifier getId() {
        return EICommon.id("mafglib");
    }

    private IHotkey getCachedKey() {
        if (cachedKey == null) {
            for (KeybindCategory category : InputEventHandler.getKeybindManager().getKeybindCategories()) {
                if (!this.category.equals(category.getCategory())) continue;
                for (IHotkey hotkey : category.getHotkeys()) {
                    if (this.keybind.equals(hotkey.getName())) {
                        cachedKey = hotkey;
                        return cachedKey;
                    }
                }
            }
        }
        return cachedKey;
    }

    public static void keyDebug() {
        EICommon.LOG.info("== MAFGLIB ACTION DEBUG ==");
        for (KeybindCategory category : InputEventHandler.getKeybindManager().getKeybindCategories()) {
            EICommon.LOG.info("Category {} by {}", category.getCategory(), category.getModName());
            for (IHotkey hotkey : category.getHotkeys()) {
                EICommon.LOG.info(" - {}(aka {}) = {}", hotkey.getName(), hotkey.getPrettyName(), hotkey.getKeybind().getKeysDisplayString());
            }
        }
    }

    public String getCategory() {
        return category;
    }

    public String getKeybind() {
        return keybind;
    }
}
