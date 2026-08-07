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
        return SHAPE;
    }
    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // 只有无果且光照足够（可选）时才有可能结果
        if (!state.getValue(FRUIT) && level.getRawBrightness(pos, 0) >= 9) {
            // 每随机刻有 10% 几率结果（可调整）
            if (random.nextFloat() < 0.1f) {
                level.setBlock(pos, state.setValue(FRUIT, true), 3);
            }
        }
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack heldStack, BlockState state, Level level, BlockPos pos,
                                           Player player, InteractionHand hand, BlockHitResult hit) {

        // 逻辑：如果手持剪刀
        if (heldStack.getItem() == Items.SHEARS) {
            if (!level.isClientSide) {
                // 掉落蓝莓丛物品
                ItemStack bushStack = new ItemStack(MiscellaneousBlocks.BLUEBERRY_BUSH_ITEM.get());
                Block.popResource(level, pos, bushStack);
                level.removeBlock(pos, false);
                // 消耗剪刀耐久
                heldStack.hurtAndBreak(1, player,
                        hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        // 逻辑：如果蓝莓丛有果实
        if (state.getValue(FRUIT)) {
            if (!level.isClientSide) {
                // 掉落蓝莓，并将状态改为无果实
                Block.popResource(level, pos, new ItemStack(FoodItems.BLUEBERRY.get()));
                level.setBlock(pos, state.setValue(FRUIT, false), 3);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        // 其他情况（手持其他物品，且蓝莓丛无果实），不处理
        return ItemInteractionResult.SUCCESS;
    }

    // 2. 处理玩家空手右键点击
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                            Player player, BlockHitResult hit) {
        // 逻辑：如果蓝莓丛有果实
        if (state.getValue(FRUIT)) {
            if (!level.isClientSide) {
                // 空手采摘：掉落蓝莓，并将状态改为无果实
                Block.popResource(level, pos, new ItemStack(FoodItems.BLUEBERRY.get()));
                level.setBlock(pos, state.setValue(FRUIT, false), 3);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // 无果实，不做任何事
        return InteractionResult.PASS;
    }

    // 存活条件：下方为草/泥土，且生物群系为丛林或森林
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


    // 中键选取返回蓝莓丛物品
    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(MiscellaneousBlocks.BLUEBERRY_BUSH.get());
    }
}