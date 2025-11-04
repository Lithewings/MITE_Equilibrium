package com.equilibrium.mixin.vanilla_itemsmixin;

import com.equilibrium.mixin.player.ClientPlayerEntityMixin;
import net.minecraft.block.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Random;

@Mixin(FluidBlock.class)
public abstract class FluidBlockMixin extends Block implements FluidDrainable {


    public FluidBlockMixin(Settings settings) {
        super(settings);
    }
    @Shadow
    public static final IntProperty LEVEL = Properties.LEVEL_15;
    @Shadow
    @Final
    protected FlowableFluid fluid;




    @Unique
    public void addParticle(World world, BlockPos pos){
        for (int i = 0; i < 10; i++) {
            world.addParticle(ParticleTypes.SMOKE,
                    pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                    0.05 ,
                    0.05 ,
                    0.05
            );
        }
    }




    @Override
    public ItemStack tryDrainFluid(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos, BlockState state) {
        //玩家捞
        if(player!=null){





            //捞源头,是岩浆,玩家没有下蹲
            if(state.get(LEVEL) == 0 && state.isOf(Blocks.LAVA )&& !player.isSneaking()){
                if(world.isClient()){
                    addParticle((World)world,pos);
                }

                if (failToGetLava(player, world, pos)) return ItemStack.EMPTY;

                //没烧坏
                if (player instanceof ServerPlayerEntity)
                    //客户端渲染会有几个帧拿到岩浆桶又因为服务端计算应该-1而又消失的动画
                    return new ItemStack(this.fluid.getBucketItem());
                else
                    return ItemStack.EMPTY;
            }
            //捞源头,是岩浆,玩家下蹲
            else if ((Integer)state.get(LEVEL) == 0 && state.isOf(Blocks.LAVA) && player.isSneaking()){
                if(world.isClient()){
                    addParticle((World)world,pos);
                }

                if (failToGetLava(player, world, pos)) return ItemStack.EMPTY;

                //没烧坏
                if (player instanceof ServerPlayerEntity) {
                    //客户端渲染会有几个帧拿到岩浆桶又因为服务端计算应该-1而又消失的动画
                    world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL_AND_REDRAW);
                    return new ItemStack(this.fluid.getBucketItem());
                }
                else
                    return ItemStack.EMPTY;
            }
            //捞源头,不是岩浆,玩家下蹲
            else if ((Integer)state.get(LEVEL) == 0 && !(state.isOf(Blocks.LAVA)) && player.isSneaking()){
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL_AND_REDRAW);
                if (player instanceof ServerPlayerEntity)
                    //客户端渲染会有几个帧拿到岩浆桶又因为服务端计算应该-1而又消失的动画
                    return new ItemStack(this.fluid.getBucketItem());
                else
                    return ItemStack.EMPTY;
            }

            //捞源头,但不捞走,不是岩浆,玩家没有下蹲
            else if ((Integer)state.get(LEVEL) == 0 && !(state.isOf(Blocks.LAVA)) && !player.isSneaking()){
                if (player instanceof ServerPlayerEntity)
                    //客户端渲染会有几个帧拿到岩浆桶又因为服务端计算应该-1而又消失的动画
                    return new ItemStack(this.fluid.getBucketItem());
                else
                    return ItemStack.EMPTY;
            }
            else
                return ItemStack.EMPTY;
        }


        //海绵吸水等
        else if ( (Integer)state.get(LEVEL) == 0) {
            return new ItemStack(this.fluid.getBucketItem());
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Unique
    private boolean failToGetLava(@NotNull PlayerEntity player, WorldAccess world, BlockPos pos) {
        if(player.getRandom().nextInt(100)<8){
            //岩浆烧坏
            if(player.getWorld().isClient){
                //只是触发动作,无意义
                player.getMainHandStack().setDamage(1);
            }
            player.getMainHandStack().setCount(player.getMainHandStack().getCount()-1);
            world.playSound(null,pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS,1,1);
            return true;
        }
        return false;
    }

}
