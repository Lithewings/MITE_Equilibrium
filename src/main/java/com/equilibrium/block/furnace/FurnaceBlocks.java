package com.equilibrium.block.furnace;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.ToIntFunction;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class FurnaceBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredBlock<TheFurnace> CLAY_FURNACE = BLOCKS.register("clay_furnace",
            () -> new TheFurnace(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE).strength(0.01F)
                    .lightLevel(createLightLevelFromBlockState(12))));

    public static final DeferredBlock<TheFurnace> OBSIDIAN_FURNACE = BLOCKS.register("obsidian_furnace",
            () -> new TheFurnace(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE).strength(0.01F)
                    .lightLevel(createLightLevelFromBlockState(11))));

    public static final DeferredBlock<TheFurnace> NETHERRACK_FURNACE = BLOCKS.register("netherrack_furnace",
            () -> new TheFurnace(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE).strength(0.01F)
                    .lightLevel(createLightLevelFromBlockState(13))));

    public static final DeferredItem<BlockItem> CLAY_FURNACE_ITEM = ITEMS.register("clay_furnace",
            () -> new BlockItem(CLAY_FURNACE.get(), new Item.Properties().stacksTo(1)));

    public static final DeferredItem<BlockItem> OBSIDIAN_FURNACE_ITEM = ITEMS.register("obsidian_furnace",
            () -> new BlockItem(OBSIDIAN_FURNACE.get(), new Item.Properties().stacksTo(1)));

    public static final DeferredItem<BlockItem> NETHERRACK_FURNACE_ITEM = ITEMS.register("netherrack_furnace",
            () -> new BlockItem(NETHERRACK_FURNACE.get(), new Item.Properties().stacksTo(1)));

    private static ToIntFunction<BlockState> createLightLevelFromBlockState(int litLevel) {
        return (blockState) -> blockState.getValue(BlockStateProperties.LIT) ? litLevel : 0;
    }
}
