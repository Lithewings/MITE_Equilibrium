package com.equilibrium.block;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.block.crafting_table.TheCraftingTableBlock;
import com.equilibrium.block.furnace_and_its_entity.TheFurnace;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.ToIntFunction;

import net.minecraft.core.component.DataComponentType;
@EventBusSubscriber(modid = OnServerInitialize.MOD_ID)
public class ModBlocksRegistry2 {

    // 工作台
    public static Block FLINT_CRAFTING_TABLE;
    public static Block COPPER_CRAFTING_TABLE;
    public static Block SILVER_CRAFTING_TABLE;
    public static Block IRON_CRAFTING_TABLE;
    public static Block DIAMOND_CRAFTING_TABLE;
    public static Block NETHERITE_CRAFTING_TABLE;

    // 熔炉
    public static Block CLAY_FURNACE;
    public static Block OBSIDIAN_FURNACE;
    public static Block NETHERRACK_FURNACE;

    @SubscribeEvent
    public static void registerBlocks(RegisterEvent event) {
        // 注册方块
        event.register(BuiltInRegistries.BLOCK.key(), helper -> {
            FLINT_CRAFTING_TABLE = new TheCraftingTableBlock(BlockBehaviour.Properties.of()
                    .strength(0.01F).sound(SoundType.WOOD));
            Registry.register(BuiltInRegistries.BLOCK,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "flint_crafting_table"),
                    FLINT_CRAFTING_TABLE);

            COPPER_CRAFTING_TABLE = new TheCraftingTableBlock(BlockBehaviour.Properties.of()
                    .strength(0.01F).sound(SoundType.WOOD));
            Registry.register(BuiltInRegistries.BLOCK,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "copper_crafting_table"),
                    COPPER_CRAFTING_TABLE);

            SILVER_CRAFTING_TABLE = new TheCraftingTableBlock(BlockBehaviour.Properties.of()
                    .strength(0.01F).sound(SoundType.WOOD));
            Registry.register(BuiltInRegistries.BLOCK,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "silver_crafting_table"),
                    SILVER_CRAFTING_TABLE);

            IRON_CRAFTING_TABLE = new TheCraftingTableBlock(BlockBehaviour.Properties.of()
                    .strength(0.01F).sound(SoundType.WOOD));
            Registry.register(BuiltInRegistries.BLOCK,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "iron_crafting_table"),
                    IRON_CRAFTING_TABLE);

            DIAMOND_CRAFTING_TABLE = new TheCraftingTableBlock(BlockBehaviour.Properties.of()
                    .strength(0.01F).sound(SoundType.WOOD));
            Registry.register(BuiltInRegistries.BLOCK,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "diamond_crafting_table"),
                    DIAMOND_CRAFTING_TABLE);

            NETHERITE_CRAFTING_TABLE = new TheCraftingTableBlock(BlockBehaviour.Properties.of()
                    .strength(0.01F).sound(SoundType.WOOD));
            Registry.register(BuiltInRegistries.BLOCK,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "netherite_crafting_table"),
                    NETHERITE_CRAFTING_TABLE);

            CLAY_FURNACE = new TheFurnace(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE).strength(0.01F)
                    .lightLevel(createLightLevelFromBlockState(12)));
            Registry.register(BuiltInRegistries.BLOCK,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "clay_furnace"),
                    CLAY_FURNACE);

            OBSIDIAN_FURNACE = new TheFurnace(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE).strength(0.01F)
                    .lightLevel(createLightLevelFromBlockState(11)));
            Registry.register(BuiltInRegistries.BLOCK,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "obsidian_furnace"),
                    OBSIDIAN_FURNACE);

            NETHERRACK_FURNACE = new TheFurnace(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE).strength(0.01F)
                    .lightLevel(createLightLevelFromBlockState(13)));
            Registry.register(BuiltInRegistries.BLOCK,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "netherrack_furnace"),
                    NETHERRACK_FURNACE);
        });

        // 注册对应的物品
        event.register(BuiltInRegistries.ITEM.key(), helper -> {
            Registry.register(BuiltInRegistries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "flint_crafting_table"),
                    new BlockItem(FLINT_CRAFTING_TABLE, new Item.Properties().stacksTo(16)));
            Registry.register(BuiltInRegistries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "copper_crafting_table"),
                    new BlockItem(COPPER_CRAFTING_TABLE, new Item.Properties().stacksTo(16)));
            Registry.register(BuiltInRegistries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "silver_crafting_table"),
                    new BlockItem(SILVER_CRAFTING_TABLE, new Item.Properties().stacksTo(16)));
            Registry.register(BuiltInRegistries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "iron_crafting_table"),
                    new BlockItem(IRON_CRAFTING_TABLE, new Item.Properties().stacksTo(16)));
            Registry.register(BuiltInRegistries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "diamond_crafting_table"),
                    new BlockItem(DIAMOND_CRAFTING_TABLE, new Item.Properties().stacksTo(16)));
            Registry.register(BuiltInRegistries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "netherite_crafting_table"),
                    new BlockItem(NETHERITE_CRAFTING_TABLE, new Item.Properties().stacksTo(16)));

            Registry.register(BuiltInRegistries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "clay_furnace"),
                    new BlockItem(CLAY_FURNACE, new Item.Properties().stacksTo(1)));
            Registry.register(BuiltInRegistries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "obsidian_furnace"),
                    new BlockItem(OBSIDIAN_FURNACE, new Item.Properties().stacksTo(1)));
            Registry.register(BuiltInRegistries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "netherrack_furnace"),
                    new BlockItem(NETHERRACK_FURNACE, new Item.Properties().stacksTo(1)));
        });
    }

    private static ToIntFunction<BlockState> createLightLevelFromBlockState(int litLevel) {
        return (blockState) -> (Boolean) blockState.getValue(BlockStateProperties.LIT) ? litLevel : 0;
    }
}