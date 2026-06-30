package com.denisjava.extended_interactions.api;

import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import net.minecraft.resources.Identifier;

public abstract class JavaInteraction extends ExtInteraction {
    public JavaInteraction(Identifier id, ExtInteractionIcon icon, EIPlugin declaringPlugin) {
        super(id, icon, declaringPlugin);
    }

    @Override
    final void _totally_not_sus_method_preventing_bad_subclasses_() {}
}
