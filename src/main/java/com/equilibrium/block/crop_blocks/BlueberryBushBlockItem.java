package com.equilibrium.block.crop_blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlueberryBushBlockItem extends BlockItem {
    public BlueberryBushBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos placePos = context.getClickedPos();
        if (!isValidPosition(level, placePos)) {
            return InteractionResult.FAIL;
        }
        return super.place(context);
    }

    private boolean isValidPosition(Level level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        boolean isSoil = belowState.getBlock() == Blocks.GRASS_BLOCK ||
                belowState.getBlock() == Blocks.DIRT ||
                belowState.getBlock() == Blocks.COARSE_DIRT ||
                belowState.getBlock() == Blocks.ROOTED_DIRT;
        if (!isSoil) return false;

        var biomeHolder = level.getBiome(pos);
        return biomeHolder.is(BiomeTags.IS_JUNGLE) || biomeHolder.is(BiomeTags.IS_FOREST);
    }
}