package com.equilibrium.mixin.crop;

import com.equilibrium.OnServerInitialize;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

import static com.equilibrium.OnServerInitialize.FERTILIZED;

@Mixin(StemBlock.class)
public abstract class StemBlockMixin extends BushBlock implements BonemealableBlock {
    protected StemBlockMixin(Properties settings) {
        super(settings);
    }
@Shadow
@Final
    public static IntegerProperty AGE;
    @Shadow
    @Final
    private ResourceKey<Block> fruit;
    @Shadow
    @Final
    private  ResourceKey<Block> attachedStem;



    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (world.getRawBrightness(pos, 0) >= 9) {
            float f = CropBlock.getGrowthSpeed(state, world, pos);


            float times = 128/16f;
            //检查农田是否具有施肥标签
            if(world.getBlockState(pos.below()).hasProperty(FERTILIZED)) {
                if (world.getBlockState(pos.below()).getValue(FERTILIZED) == true)
                    //原先的两倍加速
                    times=64f/16f;
                else
                    times=128/16f;
            }
            else
                OnServerInitialize.LOGGER.error("No such Block State called fertilized");


            if (random.nextInt((int)(times*25.0F / f) + 1) == 0) {
                int i = (Integer)state.getValue(AGE);
                if (i < 7) {
                    state = state.setValue(AGE, Integer.valueOf(i + 1));
                    world.setBlock(pos, state, Block.UPDATE_CLIENTS);
                } else {
                    Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                    BlockPos blockPos = pos.relative(direction);
                    BlockState blockState = world.getBlockState(blockPos.below());
                    if (world.getBlockState(blockPos).isAir() && (blockState.is(Blocks.FARMLAND) || blockState.is(BlockTags.DIRT))) {
                        Registry<Block> registry = world.registryAccess().registryOrThrow(Registries.BLOCK);
                        Optional<Block> optional = registry.getOptional(this.fruit);
                        Optional<Block> optional2 = registry.getOptional(this.attachedStem);
                        if (optional.isPresent() && optional2.isPresent()) {
                            world.setBlockAndUpdate(blockPos, ((Block)optional.get()).defaultBlockState());
                            world.setBlockAndUpdate(pos, ((Block)optional2.get()).defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, direction));
                        }
                    }
                }
            }
        }
    }

}
