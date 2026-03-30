package com.equilibrium.server_and_client.server.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public interface CraftingMetalPickAxeCallback {
    Event<CraftingMetalPickAxeCallback> EVENT = EventFactory.createArrayBacked(CraftingMetalPickAxeCallback.class,
            (listeners) -> (world,player) -> {
                for (CraftingMetalPickAxeCallback listener : listeners) {
                    ActionResult result = listener.interact(world,player);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult interact(World world,PlayerEntity player);
}
