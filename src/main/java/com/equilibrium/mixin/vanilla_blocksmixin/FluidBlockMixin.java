package com.equilibrium.mixin.vanilla_blocksmixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(LiquidBlock.class)
public abstract class FluidBlockMixin extends Block implements BucketPickup {


    @Shadow @Final protected FlowingFluid fluid;

    public FluidBlockMixin(Properties settings) {
        super(settings);
    }




    @Unique
    int executeCount = 0;


    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if(this.fluid== Fluids.WATER && entity instanceof ItemEntity itemEntity){
            if(itemEntity.getItem().is(Items.LAVA_BUCKET)){
                int count;
                ItemStack bucket;
                count=itemEntity.getItem().getCount();

                bucket = Items.BUCKET.getDefaultInstance();
                bucket.setCount(count);

                itemEntity.setItem(bucket);
                effect(world, pos);
                world.setBlockAndUpdate(pos, Blocks.STONE.defaultBlockState());
            }

        }



        if(this.fluid== Fluids.WATER && entity instanceof Player player){

            if(++executeCount<20)
                return;
            else executeCount=0;
            int count;
            ItemStack bucket;
            for (ItemStack itemStack : player.getInventory().items) {
                if(itemStack.is(Items.LAVA_BUCKET)) {
                    count=itemStack.getCount();
                    bucket = Items.BUCKET.getDefaultInstance();
                    bucket.setCount(count);

                    itemStack.setCount(0);
                    player.getInventory().placeItemBackInInventory(bucket);
                    world.setBlockAndUpdate(pos, Blocks.STONE.defaultBlockState());
                    effect(world, pos);
                }
            }

        }
    }

    @Unique
    private void effect(Level world, BlockPos pos) {
        if(!world.isClientSide) {
            world.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS);
        }
        else{
            // 生成烟雾粒子
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;
            for (int i = 0; i < 8; ++i) {
                world.addParticle(ParticleTypes.SMOKE,
                        x + (world.random.nextDouble() - 0.5),
                        y + (world.random.nextDouble() - 0.5),
                        z + (world.random.nextDouble() - 0.5),
                        0, 0.1, 0);
            }
        }
    }
}
