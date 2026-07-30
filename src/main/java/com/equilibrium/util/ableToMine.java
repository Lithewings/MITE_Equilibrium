package com.equilibrium.util;

import com.equilibrium.tags.ModBlockTags;
import com.equilibrium.tags.ModItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class ableToMine {
    public static int getBlockHarvestLevel(BlockState block){
        if(block.is(ModBlockTags.HARVEST_ONE)){
            return 1;
        } else if (block.is(ModBlockTags.HARVEST_TWO)) {
            return 2;
        } else if (block.is(ModBlockTags.HARVEST_THREE)) {
            return 3;
        } else if (block.is(ModBlockTags.HARVEST_FOUR)) {
            return 4;
        } else if (block.is(ModBlockTags.HARVEST_FIVE)) {
            return 5;
        }else
            return 0;
    }
    public static int getItemHarvestLevel(ItemStack stack){
        if(stack.is(ModItemTags.HARVEST_ONE)){
            return 1;
        } else if (stack.is(ModItemTags.HARVEST_TWO)) {
            return 2;
        } else if (stack.is(ModItemTags.HARVEST_THREE)) {
            return 3;
        } else if (stack.is(ModItemTags.HARVEST_FOUR)) {
            return 4;
        } else if (stack.is(ModItemTags.HARVEST_FIVE)) {
            return 5;
        }else
            //空手采集
            return 0;
    }
}
