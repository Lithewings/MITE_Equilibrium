package com.equilibrium.server_and_client.server.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface CraftingMetalPickAxeCallback {
    Event<CraftingMetalPickAxeCallback> EVENT = EventFactory.createArrayBacked(CraftingMetalPickAxeCallback.class,
            (listeners) -> (world,player) -> {
                for (CraftingMetalPickAxeCallback listener : listeners) {
                    InteractionResult result = listener.interact(world,player);

                    if(result != InteractionResult.PASS) {
                        return result;
                    }
                }
                return InteractionResult.PASS;
            });

    InteractionResult interact(Level world,Player player);
}
