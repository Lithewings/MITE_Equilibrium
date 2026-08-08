package com.equilibrium.block.crop_blocks;

import com.equilibrium.block.miscellaneous.MiscellaneousBlocks;
import com.equilibrium.item.food.FoodItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlueberryBushBlock extends Block {
    public static final BooleanProperty FRUIT = BooleanProperty.create("fruit");
    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

    public BlueberryBushBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FRUIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FRUIT);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }
    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(FRUIT) && level.getRawBrightness(pos, 0) >= 9) {
            if (random.nextFloat() < 0.1f) {
                level.setBlock(pos, state.setValue(FRUIT, true), 3);
            }
        }
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack heldStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (heldStack.getItem() == Items.SHEARS) {
            if (!level.isClientSide) {
                ItemStack bushStack = new ItemStack(MiscellaneousBlocks.BLUEBERRY_BUSH_ITEM.get());
                Block.popResource(level, pos, bushStack);
                level.removeBlock(pos, false);
                heldStack.hurtAndBreak(1, player,
                        hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (state.getValue(FRUIT)) {
            if (!level.isClientSide) {
                Block.popResource(level, pos, new ItemStack(FoodItems.BLUEBERRY.get()));
                level.setBlock(pos, state.setValue(FRUIT, false), 3);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                            Player player, BlockHitResult hit) {
        if (state.getValue(FRUIT)) {
            if (!level.isClientSide) {
                Block.popResource(level, pos, new ItemStack(FoodItems.BLUEBERRY.get()));
                level.setBlock(pos, state.setValue(FRUIT, false), 3);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        boolean isSoil = belowState.getBlock() == Blocks.GRASS_BLOCK ||
                belowState.getBlock() == Blocks.DIRT ||
                belowState.getBlock() == Blocks.COARSE_DIRT ||
                belowState.getBlock() == Blocks.ROOTED_DIRT;
        if (!isSoil) return false;

        Holder<Biome> biomeHolder = level.getBiome(pos);
        return biomeHolder.is(BiomeTags.IS_JUNGLE) || biomeHolder.is(BiomeTags.IS_FOREST);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(MiscellaneousBlocks.BLUEBERRY_BUSH.get());
    }
}