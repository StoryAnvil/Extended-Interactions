package com.denisjava.extended_interactions.api;

import com.denisjava.extended_interactions.impl.EIResultImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface EIEntityProvider {
    /**
     * Tries to supply interaction for entity.<br>
     * Provider should work same on logical client and logical server!
     * @param level Level block is located in.
     * @param user Player requested the interaction
     * @param entity Entity to supply interaction for
     * @return Provider result. See {@link EIResults#success(ExtInteraction)}, {@link EIResults#silentFailure(ExtInteraction)}, {@link EIResults#failure(ExtInteraction, Component)}
     */
    @NotNull EIResultImpl.Result collectForEntity(Level level, Player user, Entity entity);
}
