package com.equilibrium.block.material;

import net.minecraft.util.ColorRGBA;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class MaterialBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredBlock<Block> ADAMANTIUM_BLOCK = BLOCKS.register("adamantium_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(4.0f)));

    public static final DeferredBlock<Block> ANCIENT_METAL_BLOCK = BLOCKS.register("ancient_metal_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(3.0f)));


    public static final DeferredBlock<Block> COPPER_BLOCK = BLOCKS.register("copper_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(4.0f)));



    public static final DeferredBlock<Block> MITHRIL_BLOCK = BLOCKS.register("mithril_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(4.0f)));



    public static final DeferredBlock<Block> SILVER_BLOCK = BLOCKS.register("silver_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(4.0f)));



    public static final DeferredBlock<Block> GOLD_BLOCK = BLOCKS.register("gold_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(4.0f)));






    public static final DeferredItem<BlockItem> ADAMANTIUM_BLOCK_ITEM = ITEMS.register("adamantium_block",
            () -> new BlockItem(ADAMANTIUM_BLOCK.get(), new Item.Properties().stacksTo(16)));
    public static final DeferredItem<BlockItem> ANCIENT_METAL_BLOCK_ITEM = ITEMS.register("ancient_metal_block",
            () -> new BlockItem(ANCIENT_METAL_BLOCK.get(), new Item.Properties().stacksTo(16)));
    public static final DeferredItem<BlockItem> COPPER_BLOCK_ITEM = ITEMS.register("copper_block",
            () -> new BlockItem(COPPER_BLOCK.get(), new Item.Properties().stacksTo(16)));
    public static final DeferredItem<BlockItem> MITHRIL_BLOCK_ITEM = ITEMS.register("mithril_block",
            () -> new BlockItem(MITHRIL_BLOCK.get(), new Item.Properties().stacksTo(16)));
    public static final DeferredItem<BlockItem> SILVER_BLOCK_ITEM = ITEMS.register("silver_block",
            () -> new BlockItem(SILVER_BLOCK.get(), new Item.Properties().stacksTo(16)));
    public static final DeferredItem<BlockItem> GOLD_BLOCK_ITEM = ITEMS.register("gold_block",
            () -> new BlockItem(GOLD_BLOCK.get(), new Item.Properties().stacksTo(16)));
}
