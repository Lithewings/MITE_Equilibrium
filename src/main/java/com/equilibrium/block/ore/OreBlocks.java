package com.equilibrium.block.ore;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class OreBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredBlock<Block> ADAMANTIUM_ORE = BLOCKS.register("adamantium_ore",
            () -> new Block(BlockBehaviour.Properties.of().strength(4.0f)));
    public static final DeferredBlock<Block> COPPER_ORE = BLOCKS.register("copper_ore",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.0f)));
    public static final DeferredBlock<Block> SILVER_ORE = BLOCKS.register("silver_ore",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.0f)));
    public static final DeferredBlock<Block> MITHRIL_ORE = BLOCKS.register("mithril_ore",
            () -> new Block(BlockBehaviour.Properties.of().strength(4.0f)));
    public static final DeferredBlock<Block> GOLD_ORE = BLOCKS.register("gold_ore",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.0f)));

    public static final DeferredItem<BlockItem> ADAMANTIUM_ORE_ITEM = ITEMS.register("adamantium_ore",
            () -> new BlockItem(ADAMANTIUM_ORE.get(), new Item.Properties().stacksTo(16)));
    public static final DeferredItem<BlockItem> COPPER_ORE_ITEM = ITEMS.register("copper_ore",
            () -> new BlockItem(COPPER_ORE.get(), new Item.Properties().stacksTo(16)));
    public static final DeferredItem<BlockItem> MITHRIL_ORE_ITEM = ITEMS.register("mithril_ore",
            () -> new BlockItem(MITHRIL_ORE.get(), new Item.Properties().stacksTo(16)));
    public static final DeferredItem<BlockItem> SILVER_ORE_ITEM = ITEMS.register("silver_ore",
            () -> new BlockItem(SILVER_ORE.get(), new Item.Properties().stacksTo(16)));
    public static final DeferredItem<BlockItem> GOLD_ORE_ITEM = ITEMS.register("gold_ore",
            () -> new BlockItem(GOLD_ORE.get(), new Item.Properties().stacksTo(16)));
}
