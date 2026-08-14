package com.equilibrium.block;


import com.equilibrium.OnServerInitialize;

import com.equilibrium.block.crafting_table.TheCraftingTableBlock;

import com.equilibrium.block.furnace_and_its_entity.TheFurnace;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

import java.util.function.ToIntFunction;

public class ModBlocksRegistry2 {

    //tables
    public static Block FLINT_CRAFTING_TABLE = new TheCraftingTableBlock((AbstractBlock.Settings.create().strength(0.01F).sounds(BlockSoundGroup.WOOD)));//燧石工作台
    public static Block COPPER_CRAFTING_TABLE = new TheCraftingTableBlock((AbstractBlock.Settings.create().strength(0.01F).sounds(BlockSoundGroup.WOOD)));//铜工作台
    public static Block SILVER_CRAFTING_TABLE = new TheCraftingTableBlock((AbstractBlock.Settings.create().strength(0.01F).sounds(BlockSoundGroup.WOOD)));//银工作台

    public static Block IRON_CRAFTING_TABLE = new TheCraftingTableBlock((AbstractBlock.Settings.create().strength(0.01F).sounds(BlockSoundGroup.WOOD)));//铁工作台
    public static Block MITHRIL_CRAFTING_TABLE = new TheCraftingTableBlock((AbstractBlock.Settings.create().strength(0.01F).sounds(BlockSoundGroup.WOOD)));//秘银工作台
    public static Block ADAMANTIUM_CRAFTING_TABLE = new TheCraftingTableBlock((AbstractBlock.Settings.create().strength(0.01F).sounds(BlockSoundGroup.WOOD)));//艾德曼工作台






    public static Block CLAY_FURNACE = new TheFurnace((AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE).strength(0.01F).luminance(createLightLevelFromBlockState(12)).sounds(BlockSoundGroup.STONE)));//粘土熔炉
    public static Block OBSIDIAN_FURNACE = new TheFurnace((AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE).strength(0.01F).luminance(createLightLevelFromBlockState(11)).sounds(BlockSoundGroup.STONE)));//黑曜石熔炉
    public static Block NETHERRACK_FURNACE = new TheFurnace((AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE).strength(0.01F).luminance(createLightLevelFromBlockState(13)).sounds(BlockSoundGroup.STONE)));//地狱岩熔炉

    public static void registerBlocks() {
        //tables
        Registry.register(Registries.BLOCK, Identifier.of(OnServerInitialize.MOD_ID, "flint_crafting_table"), FLINT_CRAFTING_TABLE);
        Registry.register(Registries.BLOCK, Identifier.of(OnServerInitialize.MOD_ID, "copper_crafting_table"), COPPER_CRAFTING_TABLE);
        Registry.register(Registries.BLOCK, Identifier.of(OnServerInitialize.MOD_ID, "silver_crafting_table"), SILVER_CRAFTING_TABLE);


        Registry.register(Registries.BLOCK, Identifier.of(OnServerInitialize.MOD_ID, "iron_crafting_table"), IRON_CRAFTING_TABLE);
        Registry.register(Registries.BLOCK, Identifier.of(OnServerInitialize.MOD_ID, "mithril_crafting_table"), MITHRIL_CRAFTING_TABLE);
        Registry.register(Registries.BLOCK, Identifier.of(OnServerInitialize.MOD_ID, "adamantium_crafting_table"), ADAMANTIUM_CRAFTING_TABLE);

        Registry.register(Registries.BLOCK, Identifier.of(OnServerInitialize.MOD_ID, "clay_furnace"), CLAY_FURNACE);
        Registry.register(Registries.BLOCK, Identifier.of(OnServerInitialize.MOD_ID, "obsidian_furnace"), OBSIDIAN_FURNACE);
        Registry.register(Registries.BLOCK, Identifier.of(OnServerInitialize.MOD_ID, "netherrack_furnace"), NETHERRACK_FURNACE);

    }

    public static void registerBlockItems() {
        //tables
        Registry.register(Registries.ITEM, Identifier.of( OnServerInitialize.MOD_ID, "flint_crafting_table"), new BlockItem(FLINT_CRAFTING_TABLE, new Item.Settings().maxCount(16)));
        Registry.register(Registries.ITEM, Identifier.of( OnServerInitialize.MOD_ID, "copper_crafting_table"), new BlockItem(COPPER_CRAFTING_TABLE, new Item.Settings().maxCount(16)));
        Registry.register(Registries.ITEM, Identifier.of( OnServerInitialize.MOD_ID, "silver_crafting_table"), new BlockItem(SILVER_CRAFTING_TABLE, new Item.Settings().maxCount(16)));
        Registry.register(Registries.ITEM, Identifier.of( OnServerInitialize.MOD_ID, "iron_crafting_table"), new BlockItem(IRON_CRAFTING_TABLE, new Item.Settings().maxCount(16)));
        Registry.register(Registries.ITEM, Identifier.of( OnServerInitialize.MOD_ID, "mithril_crafting_table"), new BlockItem(MITHRIL_CRAFTING_TABLE, new Item.Settings().maxCount(16)));
        Registry.register(Registries.ITEM, Identifier.of( OnServerInitialize.MOD_ID, "adamantium_crafting_table"), new BlockItem(ADAMANTIUM_CRAFTING_TABLE, new Item.Settings().maxCount(16)));

        Registry.register(Registries.ITEM, Identifier.of( OnServerInitialize.MOD_ID, "clay_furnace"), new BlockItem(CLAY_FURNACE, new Item.Settings().maxCount(1)));
        Registry.register(Registries.ITEM, Identifier.of( OnServerInitialize.MOD_ID, "obsidian_furnace"), new BlockItem(OBSIDIAN_FURNACE, new Item.Settings().maxCount(1)));
        Registry.register(Registries.ITEM, Identifier.of( OnServerInitialize.MOD_ID, "netherrack_furnace"), new BlockItem(NETHERRACK_FURNACE, new Item.Settings().maxCount(1)));
    }

    public static void registerFuels() {
        FuelRegistry.INSTANCE.add(FLINT_CRAFTING_TABLE, 300);
        FuelRegistry.INSTANCE.add(COPPER_CRAFTING_TABLE, 300);
        FuelRegistry.INSTANCE.add(SILVER_CRAFTING_TABLE, 300);
        FuelRegistry.INSTANCE.add(IRON_CRAFTING_TABLE, 300);
        FuelRegistry.INSTANCE.add(MITHRIL_CRAFTING_TABLE, 300);
        FuelRegistry.INSTANCE.add(ADAMANTIUM_CRAFTING_TABLE, 300);
    }


    private static ToIntFunction<BlockState> createLightLevelFromBlockState(int litLevel) {
        return (blockState) -> (Boolean)blockState.get(Properties.LIT) ? litLevel : 0;
    }

}
