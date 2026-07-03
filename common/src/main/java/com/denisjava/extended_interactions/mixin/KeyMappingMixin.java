package com.denisjava.extended_interactions.mixin;

import com.denisjava.extended_interactions.util.EIKeyMapping;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(KeyMapping.class)
public class KeyMappingMixin implements EIKeyMapping {
    @Shadow
    @Final
    private static Map<String, KeyMapping> ALL;

    @Shadow
    private int clickCount;

    @Override
    public Map<String, KeyMapping> ei$getAll() {
        return ALL;
    }

    @Override
    public void ei$addClick() {
        ++clickCount;
    }
}
