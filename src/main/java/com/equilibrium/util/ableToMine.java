package com.equilibrium.util;

import com.equilibrium.tags.ModBlockTags;
import com.equilibrium.tags.ModItemTags;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

public class ableToMine {
    public static int getBlockHarvestLevel(BlockState block){
        if(block.isIn(ModBlockTags.HARVEST_ONE)){
            return 1;
        } else if (block.isIn(ModBlockTags.HARVEST_TWO)) {
            return 2;
        } else if (block.isIn(ModBlockTags.HARVEST_THREE)) {
            return 3;
        } else if (block.isIn(ModBlockTags.HARVEST_FOUR)) {
            return 4;
        } else if (block.isIn(ModBlockTags.HARVEST_FIVE)) {
            return 5;
        }else
            return 0;
    }
    public static int getItemHarvestLevel(ItemStack stack){
        if(stack.isIn(ModItemTags.HARVEST_ONE)){
            return 1;
        } else if (stack.isIn(ModItemTags.HARVEST_TWO)) {
            return 2;
        } else if (stack.isIn(ModItemTags.HARVEST_THREE)) {
            return 3;
        } else if (stack.isIn(ModItemTags.HARVEST_FOUR)) {
            return 4;
        } else if (stack.isIn(ModItemTags.HARVEST_FIVE)) {
            return 5;
        }else
            //空手采集
            return 0;
    }
}
