package com.equilibrium.block.crafting_table;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class CraftingTableBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredBlock<TheCraftingTableBlock> FLINT_CRAFTING_TABLE = BLOCKS.register("flint_crafting_table",
            () -> new TheCraftingTableBlock(BlockBehaviour.Properties.of()
                    .strength(0.01F).sound(SoundType.WOOD)));

    public static final DeferredBlock<TheCraftingTableBlock> COPPER_CRAFTING_TABLE = BLOCKS.register("copper_crafting_table",
            () -> new TheCraftingTableBlock(BlockBehaviour.Properties.of()
                    .strength(0.01F).sound(SoundType.WOOD)));

    public static final DeferredBlock<TheCraftingTableBlock> SILVER_CRAFTING_TABLE = BLOCKS.register("silver_crafting_table",
            () -> new TheCraftingTableBlock(BlockBehaviour.Properties.of()
                    .strength(0.01F).sound(SoundType.WOOD)));

    public static final DeferredBlock<TheCraftingTableBlock> IRON_CRAFTING_TABLE = BLOCKS.register("iron_crafting_table",
            () -> new TheCraftingTableBlock(BlockBehaviour.Properties.of()
                    .strength(0.01F).sound(SoundType.WOOD)));

    public static final DeferredBlock<TheCraftingTableBlock> DIAMOND_CRAFTING_TABLE = BLOCKS.register("diamond_crafting_table",
            () -> new TheCraftingTableBlock(BlockBehaviour.Properties.of()
                    .strength(0.01F).sound(SoundType.WOOD)));

    public static final DeferredBlock<TheCraftingTableBlock> NETHERITE_CRAFTING_TABLE = BLOCKS.register("netherite_crafting_table",
            () -> new TheCraftingTableBlock(BlockBehaviour.Properties.of()
                    .strength(0.01F).sound(SoundType.WOOD)));

    public static final DeferredItem<BlockItem> FLINT_CRAFTING_TABLE_ITEM = ITEMS.register("flint_crafting_table",
            () -> new BlockItem(FLINT_CRAFTING_TABLE.get(), new Item.Properties().stacksTo(16)));

    public static final DeferredItem<BlockItem> COPPER_CRAFTING_TABLE_ITEM = ITEMS.register("copper_crafting_table",
            () -> new BlockItem(COPPER_CRAFTING_TABLE.get(), new Item.Properties().stacksTo(16)));

    public static final DeferredItem<BlockItem> SILVER_CRAFTING_TABLE_ITEM = ITEMS.register("silver_crafting_table",
            () -> new BlockItem(SILVER_CRAFTING_TABLE.get(), new Item.Properties().stacksTo(16)));

    public static final DeferredItem<BlockItem> IRON_CRAFTING_TABLE_ITEM = ITEMS.register("iron_crafting_table",
            () -> new BlockItem(IRON_CRAFTING_TABLE.get(), new Item.Properties().stacksTo(16)));

    public static final DeferredItem<BlockItem> DIAMOND_CRAFTING_TABLE_ITEM = ITEMS.register("diamond_crafting_table",
            () -> new BlockItem(DIAMOND_CRAFTING_TABLE.get(), new Item.Properties().stacksTo(16)));

    public static final DeferredItem<BlockItem> NETHERITE_CRAFTING_TABLE_ITEM = ITEMS.register("netherite_crafting_table",
            () -> new BlockItem(NETHERITE_CRAFTING_TABLE.get(), new Item.Properties().stacksTo(16)));
}
