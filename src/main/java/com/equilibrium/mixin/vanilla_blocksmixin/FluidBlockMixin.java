package com.equilibrium.mixin.vanilla_blocksmixin;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(FluidBlock.class)
public abstract class FluidBlockMixin extends Block implements FluidDrainable {


    @Shadow @Final protected FlowableFluid fluid;

    public FluidBlockMixin(Settings settings) {
        super(settings);
    }




    @Unique
    int executeCount = 0;


    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if(this.fluid== Fluids.WATER && entity instanceof ItemEntity itemEntity){
            if(itemEntity.getStack().isOf(Items.LAVA_BUCKET)){
                int count;
                ItemStack bucket;
                count=itemEntity.getStack().getCount();

                bucket = Items.BUCKET.getDefaultStack();
                bucket.setCount(count);

                itemEntity.setStack(bucket);
                effect(world, pos);
                world.setBlockState(pos, Blocks.STONE.getDefaultState());
            }

        }



        if(this.fluid== Fluids.WATER && entity instanceof PlayerEntity player){

            if(++executeCount<20)
                return;
            else executeCount=0;
            int count;
            ItemStack bucket;
            for (ItemStack itemStack : player.getInventory().main) {
                if(itemStack.isOf(Items.LAVA_BUCKET)) {
                    count=itemStack.getCount();
                    bucket = Items.BUCKET.getDefaultStack();
                    bucket.setCount(count);

                    itemStack.setCount(0);
                    player.getInventory().offerOrDrop(bucket);
                    world.setBlockState(pos, Blocks.STONE.getDefaultState());
                    effect(world, pos);
                }
            }

        }
    }

    @Unique
    private void effect(World world, BlockPos pos) {
        if(!world.isClient) {
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS);
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
