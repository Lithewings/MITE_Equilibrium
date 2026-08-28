package com.equilibrium.mixin.vanilla_itemsmixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FlowingFluid;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlock.class)
public abstract class FluidBlockMixin extends Block implements BucketPickup {


    public FluidBlockMixin(Properties settings) {
        super(settings);
    }
    @Shadow
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL;
    @Shadow
    @Final
    protected FlowingFluid fluid;




    @Unique
    public void addParticle(Level world, BlockPos pos){
        for (int i = 0; i < 10; i++) {
            world.addParticle(ParticleTypes.SMOKE,
                    pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                    0.05 ,
                    0.05 ,
                    0.05
            );
        }
    }




    @Inject(method = "pickupBlock",at = @At("HEAD"),cancellable = true)
    public void pickupBlock(Player player, LevelAccessor world, BlockPos pos, BlockState state, CallbackInfoReturnable<ItemStack> cir) {
        cir.cancel();
        //玩家捞
        if(player!=null){

            //捞源头,是岩浆,玩家没有下蹲
            if(state.getValue(LEVEL) == 0 && state.is(Blocks.LAVA )&& !player.isShiftKeyDown()){
                if(world.isClientSide()){
                    addParticle((Level)world,pos);
                }

                if (failToGetLava(player, world, pos))
                    cir.setReturnValue(ItemStack.EMPTY);

                //没烧坏
                if (player instanceof ServerPlayer)
                    //客户端渲染会有几个帧拿到岩浆桶又因为服务端计算应该-1而又消失的动画
                    cir.setReturnValue(new ItemStack(this.fluid.getBucket()));
                else
                    cir.setReturnValue(ItemStack.EMPTY);
            }
            //捞源头,是岩浆,玩家下蹲
            else if ((Integer)state.getValue(LEVEL) == 0 && state.is(Blocks.LAVA) && player.isShiftKeyDown()){
                if(world.isClientSide()){
                    addParticle((Level)world,pos);
                }

                if (failToGetLava(player, world, pos))
                    cir.setReturnValue(ItemStack.EMPTY);

                //没烧坏
                if (player instanceof ServerPlayer) {
                    //客户端渲染会有几个帧拿到岩浆桶又因为服务端计算应该-1而又消失的动画
                    world.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
                    cir.setReturnValue(new ItemStack(this.fluid.getBucket()));
                }
                else
                    cir.setReturnValue(ItemStack.EMPTY);
            }
            //捞源头,不是岩浆,玩家下蹲
            else if ((Integer)state.getValue(LEVEL) == 0 && !(state.is(Blocks.LAVA)) && player.isShiftKeyDown()){
                world.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
                if (player instanceof ServerPlayer)
                    //客户端渲染会有几个帧拿到岩浆桶又因为服务端计算应该-1而又消失的动画
                    cir.setReturnValue(new ItemStack(this.fluid.getBucket()));
                else
                    cir.setReturnValue(ItemStack.EMPTY);
            }

            //捞源头,但不捞走,不是岩浆,玩家没有下蹲
            else if ((Integer)state.getValue(LEVEL) == 0 && !(state.is(Blocks.LAVA)) && !player.isShiftKeyDown()){
                if (player instanceof ServerPlayer)
                    //客户端渲染会有几个帧拿到岩浆桶又因为服务端计算应该-1而又消失的动画
                    cir.setReturnValue(new ItemStack(this.fluid.getBucket()));
                else
                    cir.setReturnValue(ItemStack.EMPTY);
            }
            else
                cir.setReturnValue(ItemStack.EMPTY);
        }


        //海绵吸水等
        else if ( (Integer)state.getValue(LEVEL) == 0) {
            cir.setReturnValue(new ItemStack(this.fluid.getBucket()));
        } else {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @Unique
    private boolean failToGetLava(@NotNull Player player, LevelAccessor world, BlockPos pos) {
        if(player.getRandom().nextInt(100)<8){
            //岩浆烧坏
            if(player.level().isClientSide){
                //只是触发动作,无意义
                player.getMainHandItem().setDamageValue(1);
            }
            player.getMainHandItem().setCount(player.getMainHandItem().getCount()-1);
            world.playSound(null,pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS,1,1);
            return true;
        }
        return false;
    }

}
