package com.denisjava.extended_interactions.api.providers;

import com.denisjava.extended_interactions.util.ThrowableEIResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@FunctionalInterface
public interface EIEntityProvider {
    /**
     * Tries to supply interaction for entity.<br>
     * Provider should work same on logical client and logical server!
     *
     * @param collector Object to return {@link EIResult}s to
     * @param level     Level block is located in.
     * @param user      Player requested the interaction
     * @param entity    Entity to supply interaction for
     * @throws ThrowableEIResult Use {@link EIResult#throwNow()} to add {@link EIResult} to the collector immediately
     */
    void collectForEntity(EIResultCollector collector, Level level, Player user, Entity entity) throws ThrowableEIResult;
}
