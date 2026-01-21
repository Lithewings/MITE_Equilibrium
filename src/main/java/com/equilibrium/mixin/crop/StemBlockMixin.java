package com.equilibrium.mixin.crop;

import com.equilibrium.OnServerInitialize;
import net.minecraft.block.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

import static com.equilibrium.OnServerInitialize.FERTILIZED;

@Mixin(StemBlock.class)
public abstract class StemBlockMixin extends PlantBlock implements Fertilizable {
    protected StemBlockMixin(Settings settings) {
        super(settings);
    }
@Shadow
@Final
    public static IntProperty AGE;
    @Shadow
    @Final
    private RegistryKey<Block> gourdBlock;
    @Shadow
    @Final
    private  RegistryKey<Block> attachedStemBlock;
    @Shadow
    @Final
    private RegistryKey<Item> pickBlockItem;
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getBaseLightLevel(pos, 0) >= 9) {
            float f = CropBlock.getAvailableMoisture(this, world, pos);


            float times = 128/16f;
            //检查农田是否具有施肥标签
            if(world.getBlockState(pos.down()).contains(FERTILIZED)) {
                if (world.getBlockState(pos.down()).get(FERTILIZED) == true)
                    //原先的两倍加速
                    times=64f/16f;
                else
                    times=128/16f;
            }
            else
                OnServerInitialize.LOGGER.error("No such Block State called fertilized");


            if (random.nextInt((int)(times*25.0F / f) + 1) == 0) {
                int i = (Integer)state.get(AGE);
                if (i < 7) {
                    state = state.with(AGE, Integer.valueOf(i + 1));
                    world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
                } else {
                    Direction direction = Direction.Type.HORIZONTAL.random(random);
                    BlockPos blockPos = pos.offset(direction);
                    BlockState blockState = world.getBlockState(blockPos.down());
                    if (world.getBlockState(blockPos).isAir() && (blockState.isOf(Blocks.FARMLAND) || blockState.isIn(BlockTags.DIRT))) {
                        Registry<Block> registry = world.getRegistryManager().get(RegistryKeys.BLOCK);
                        Optional<Block> optional = registry.getOrEmpty(this.gourdBlock);
                        Optional<Block> optional2 = registry.getOrEmpty(this.attachedStemBlock);
                        if (optional.isPresent() && optional2.isPresent()) {
                            world.setBlockState(blockPos, ((Block)optional.get()).getDefaultState());
                            world.setBlockState(pos, ((Block)optional2.get()).getDefaultState().with(HorizontalFacingBlock.FACING, direction));
                        }
                    }
                }
            }
        }
    }

}
