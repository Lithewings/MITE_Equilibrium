package com.equilibrium.craft_time_register;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;

public class UseBlock {
    public static void init(){
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            Block block = world.getBlockState(hitResult.getBlockPos()).getBlock();
            if (!player.getWorld().isClient){
                if(!player.isCreative()){
                    if(block == Blocks.CRAFTING_TABLE){
                        world.removeBlock(hitResult.getBlockPos(),true);
                    }
                }
            }
            return ActionResult.PASS;
        });
    }
}
