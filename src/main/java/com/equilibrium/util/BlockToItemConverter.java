package com.equilibrium.util;

import com.equilibrium.block.ModBlocksRegistry;
import com.equilibrium.item.Metal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class BlockToItemConverter {
    // 创建一个Map来存储Block和对应的Item,用于矿石掉落
    private final Map<Block, Item> blockItemMap = new HashMap<>();

    public BlockToItemConverter() {

        // 向Map中添加Blocks和对应的Items
        blockItemMap.put(Blocks.IRON_ORE, Items.RAW_IRON);
        blockItemMap.put(Blocks.DEEPSLATE_IRON_ORE, Items.RAW_IRON);

        blockItemMap.put(Blocks.COPPER_ORE, Items.RAW_COPPER);
        blockItemMap.put(Blocks.DEEPSLATE_COPPER_ORE, Items.RAW_COPPER);

        blockItemMap.put(Blocks.GOLD_ORE, Items.RAW_GOLD);
        blockItemMap.put(Blocks.DEEPSLATE_GOLD_ORE, Items.RAW_GOLD);

        blockItemMap.put(Blocks.COAL_ORE, Items.COAL);
        blockItemMap.put(Blocks.DEEPSLATE_COAL_ORE, Items.COAL);

        blockItemMap.put(Blocks.DIAMOND_ORE, Items.DIAMOND);
        blockItemMap.put(Blocks.DEEPSLATE_DIAMOND_ORE, Items.DIAMOND);

        blockItemMap.put(Blocks.EMERALD_ORE, Items.EMERALD);
        blockItemMap.put(Blocks.DEEPSLATE_EMERALD_ORE, Items.EMERALD);

        blockItemMap.put(ModBlocksRegistry.ADAMANTIUM_ORE, Metal.ADAMANTIUM_RAW);

        blockItemMap.put(ModBlocksRegistry.COPPER_ORE, Items.RAW_COPPER);

        blockItemMap.put(ModBlocksRegistry.GOLD_ORE, Items.RAW_GOLD);
        blockItemMap.put(ModBlocksRegistry.SILVER_ORE, Metal.SILVER_RAW);
        blockItemMap.put(ModBlocksRegistry.MITHRIL_ORE, Metal.MITHRIL_RAW);

        //多次掉落
        blockItemMap.put(Blocks.REDSTONE_ORE, Items.REDSTONE);
        blockItemMap.put(Blocks.DEEPSLATE_REDSTONE_ORE, Items.REDSTONE);

        blockItemMap.put(Blocks.LAPIS_ORE, Items.LAPIS_LAZULI);
        blockItemMap.put(Blocks.DEEPSLATE_LAPIS_ORE, Items.LAPIS_LAZULI);

        blockItemMap.put(Blocks.NETHER_GOLD_ORE, Items.GOLD_NUGGET);
        blockItemMap.put(Blocks.GILDED_BLACKSTONE, Items.GOLD_NUGGET);


        blockItemMap.put(Blocks.NETHER_QUARTZ_ORE, Items.QUARTZ);





        // 可以添加更多的Block和Item对应关系
    }

    // 将Block转换为对应Item的方法
    public Item convertBlockToItem(Block block) {
        return blockItemMap.get(block);
    }

}
