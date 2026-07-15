package com.denisjava.extended_interactions.mixin;

//? if <1.21.11
//import net.minecraft.world.entity.npc.Villager;
//? if >=1.21.11
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Villager.class)
public interface EIVillager {
    @Invoker("startTrading")
    void ei$startTrading(Player player);
}
