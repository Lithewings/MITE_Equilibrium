package com.equilibrium.tags;

import com.equilibrium.MITEequilibrium;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModBlockTags {

    public static final TagKey<Block> SHOULD_BE_SOFT = of("should_be_soft");

    public static final TagKey<Block> STONE_LIKE_240 = of("stone_like_240");
    public static final TagKey<Block> STONE_LIKE_360 = of("stone_like_360");
    public static final TagKey<Block> STONE_LIKE_480 = of("stone_like_480");

    public static final TagKey<Block> LOG_120 = of("log_like_120");

    public static final TagKey<Block> NORMAL_60 = of("normal_60");

    public static final TagKey<Block> NORMAL_30 = of("normal_30");

    //稿子加速挖掘
    public static final TagKey<Block> NEED_PICKAXE = of("block_need_pickaxe.json");

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
        return TagKey.of(RegistryKeys.BLOCK, Identifier.of(MITEequilibrium.MOD_ID,id));
    }


    public static void registerModBlockTags(){
    }

}
