package com.equilibrium.tags;

import com.equilibrium.OnServerInitialize;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModBlockTags {

    public static final TagKey<Block> SHOULD_BE_SOFT = of("should_be_soft");


    //玻璃方块
    public static final TagKey<Block> GLASS_MADE = of("glass_made");

    //1级采集等级,定义为原版可以空手采集但这里不行的方块
    public static final TagKey<Block> HARVEST_ONE = of("block_harvest_1");
    public static final TagKey<Block> HARVEST_TWO = of("block_harvest_2");
    public static final TagKey<Block> HARVEST_THREE = of("block_harvest_3");
    public static final TagKey<Block> HARVEST_FOUR = of("block_harvest_4");
    public static final TagKey<Block> HARVEST_FIVE = of("block_harvest_5");


    public static final TagKey<Block> TRANSPARENT_FOR_ZOMBIE= of("transparent_for_zombie");

    public static final TagKey<Block> ORE = of("ore");

    //功能性方块,采集速度很快
    public static final TagKey<Block> CATEGORY = of("category");

    private static TagKey<Block> of(String id) {
        return TagKey.of(RegistryKeys.BLOCK, Identifier.of(OnServerInitialize.MOD_ID,id));
    }


    public static void registerModBlockTags(){
    }

}
