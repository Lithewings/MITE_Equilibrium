package com.equilibrium.item.food;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import static com.equilibrium.OnServerInitialize.FERTILIZED;
import static com.equilibrium.server_and_client.server.event.CropIllnessEvent.CROP_BLOCK_POS;
import static com.equilibrium.server_and_client.server.event.CropIllnessEvent.updateCropBlockPos;

public class ManureItem extends Item {
    public static final int field_30851 = 3;
    public static final int field_30852 = 1;
    public static final int field_30853 = 3;


    public ManureItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockPos blockPos2 = blockPos.relative(context.getClickedFace());
        if (useOnFertilizable(context.getItemInHand(), world, blockPos)) {
            if (!world.isClientSide) {

                //————见BoneItemMixin
                //考虑情景:骨粉选中农田上的农作物时,对其土地施肥也能生效
                BlockPos blockPos3 = context.getClickedPos().below();
                BlockState state = world.getBlockState(blockPos3);
                //注入的位置,stack数量-1的逻辑已经完成
                if(state.hasProperty(FERTILIZED) && state.getValue(FERTILIZED)==false){
                    world.setBlock(blockPos3, state.setValue(FERTILIZED, true), Block.UPDATE_ALL);
                }
                //————————————————————


                context.getPlayer().gameEvent(GameEvent.ITEM_INTERACT_FINISH);
                world.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, blockPos, 15);
            }

            return InteractionResult.sidedSuccess(world.isClientSide);
        } else {
            BlockState blockState = world.getBlockState(blockPos);
            boolean bl = blockState.isFaceSturdy(world, blockPos, context.getClickedFace());
            if (bl && useOnGround(context.getItemInHand(), world, blockPos2, context.getClickedFace())) {
                if (!world.isClientSide) {
                    context.getPlayer().gameEvent(GameEvent.ITEM_INTERACT_FINISH);
                    world.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, blockPos2, 15);
                }

                return InteractionResult.sidedSuccess(world.isClientSide);
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    public static boolean useOnFertilizable(ItemStack stack, Level world, BlockPos pos) {
        //————见BoneItemMixin
        if(world.random.nextInt(8)!=0) {
            stack.shrink(1);
            //正常创建粒子,但执行加速生长的逻辑
            return true;

        }
        //————————————————————


        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof BonemealableBlock fertilizable && fertilizable.isValidBonemealTarget(world, pos, blockState)) {
            if (world instanceof ServerLevel serverWorld) {
                if(blockState.getBlock() instanceof CropBlock cropBlock){
                    //施加加速生长的逻辑
                    cropBlock.performBonemeal((ServerLevel)world, world.random, pos, blockState);

                    //更新状态
                    updateCropBlockPos(serverWorld);
                    //维持原先的生病状态
                    CROP_BLOCK_POS.getOrDefault(pos,false);

                }
                else if (fertilizable.isBonemealSuccess(world, world.random, pos, blockState)) {
                    fertilizable.performBonemeal((ServerLevel)world, world.random, pos, blockState);
                }

                stack.shrink(1);
            }

            return true;
        }

        return false;
    }

    public static boolean useOnGround(ItemStack stack, Level world, BlockPos blockPos, @Nullable Direction facing) {
        if (world.getBlockState(blockPos).is(Blocks.WATER) && world.getFluidState(blockPos).getAmount() == 8) {
            if (!(world instanceof ServerLevel)) {
                return true;
            } else {
                RandomSource random = world.getRandom();

                label78:
                for (int i = 0; i < 128; i++) {
                    BlockPos blockPos2 = blockPos;
                    BlockState blockState = Blocks.SEAGRASS.defaultBlockState();

                    for (int j = 0; j < i / 16; j++) {
                        blockPos2 = blockPos2.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                        if (world.getBlockState(blockPos2).isCollisionShapeFullBlock(world, blockPos2)) {
                            continue label78;
                        }
                    }

                    Holder<Biome> registryEntry = world.getBiome(blockPos2);
                    if (registryEntry.is(BiomeTags.PRODUCES_CORALS_FROM_BONEMEAL)) {
                        if (i == 0 && facing != null && facing.getAxis().isHorizontal()) {
                            blockState = (BlockState) BuiltInRegistries.BLOCK
                                    .getRandomElementOf(BlockTags.WALL_CORALS, world.random)
                                    .map(blockEntry -> ((Block)blockEntry.value()).defaultBlockState())
                                    .orElse(blockState);
                            if (blockState.hasProperty(BaseCoralWallFanBlock.FACING)) {
                                blockState = blockState.setValue(BaseCoralWallFanBlock.FACING, facing);
                            }
                        } else if (random.nextInt(4) == 0) {
                            blockState = (BlockState)BuiltInRegistries.BLOCK
                                    .getRandomElementOf(BlockTags.UNDERWATER_BONEMEALS, world.random)
                                    .map(blockEntry -> ((Block)blockEntry.value()).defaultBlockState())
                                    .orElse(blockState);
                        }
                    }

                    if (blockState.is(BlockTags.WALL_CORALS, state -> state.hasProperty(BaseCoralWallFanBlock.FACING))) {
                        for (int k = 0; !blockState.canSurvive(world, blockPos2) && k < 4; k++) {
                            blockState = blockState.setValue(BaseCoralWallFanBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random));
                        }
                    }

                    if (blockState.canSurvive(world, blockPos2)) {
                        BlockState blockState2 = world.getBlockState(blockPos2);
                        if (blockState2.is(Blocks.WATER) && world.getFluidState(blockPos2).getAmount() == 8) {
                            world.setBlock(blockPos2, blockState, Block.UPDATE_ALL);
                        } else if (blockState2.is(Blocks.SEAGRASS) && random.nextInt(10) == 0) {
                            ((BonemealableBlock)Blocks.SEAGRASS).performBonemeal((ServerLevel)world, random, blockPos2, blockState2);
                        }
                    }
                }

                stack.shrink(1);
                return true;
            }
        } else {
            return false;
        }
    }

    public static void createParticles(LevelAccessor world, BlockPos pos, int count) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof BonemealableBlock fertilizable) {
            BlockPos blockPos = fertilizable.getParticlePos(pos);
            switch (fertilizable.getType()) {
                case NEIGHBOR_SPREADER:
                    ParticleUtils.spawnParticles(world, blockPos, count * 3, 3.0, 1.0, false, ParticleTypes.HAPPY_VILLAGER);
                    break;
                case GROWER:
                    ParticleUtils.spawnParticleInBlock(world, blockPos, count, ParticleTypes.HAPPY_VILLAGER);
            }
        } else if (blockState.is(Blocks.WATER)) {
            ParticleUtils.spawnParticles(world, pos, count * 3, 3.0, 1.0, false, ParticleTypes.HAPPY_VILLAGER);
        }
    }
}
