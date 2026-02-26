package com.equilibrium.mixin.vanilla_blocksmixin.tables;

import com.equilibrium.OnServerInitialize;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.logging.Logger;

import static net.minecraft.sound.SoundCategory.BLOCKS;

@Mixin(AnvilBlock.class)
public abstract class AnvilBlockMixin extends FallingBlock {
    public AnvilBlockMixin(Settings settings) {
        super(settings);

    }
    @Inject(method = "<init>",at = @At("TAIL"))
    public void AnvilBlock(Settings settings, CallbackInfo ci) {
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(DURABILITY,128));
    }


    @Unique
    private static final IntProperty DURABILITY = IntProperty.of("durability",0,128);


    @Inject(method = "appendProperties",at = @At("TAIL"))
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(DURABILITY);
    }



    @Shadow
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;


    @Inject(method = "onUse",at = @At("HEAD"), cancellable = true)
    public void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        cir.cancel();

        OnServerInitialize.LOGGER.info(world.getBlockState(pos).toString());



        if (world.isClient) {
            cir.setReturnValue(ActionResult.SUCCESS);
        } else if (player.getMainHandStack().isOf(Items.IRON_BLOCK)) {
            BlockState newAnvilBlock = null;
            int count = player.getMainHandStack().getCount();
            //损坏的第一阶段—>完好无损
            if (state.getBlock() == Blocks.CHIPPED_ANVIL){
                newAnvilBlock = Blocks.ANVIL.getDefaultState().with(FACING, (Direction) state.get(FACING));
                player.playSound(SoundEvents.BLOCK_ANVIL_USE);
            }
            //损坏的第二阶段—>第一阶段
            else if(state.getBlock() == Blocks.DAMAGED_ANVIL){
                newAnvilBlock =Blocks.CHIPPED_ANVIL.getDefaultState().with(FACING, (Direction) state.get(FACING));
                player.playSound(SoundEvents.BLOCK_ANVIL_USE);
            }
            else {
                //如果是完好无损的铁砧,正常交互
                player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
                player.incrementStat(Stats.INTERACT_WITH_ANVIL);
                cir.setReturnValue(ActionResult.SUCCESS);;
            }
            world.setBlockState(pos, newAnvilBlock);
            //消耗一个铁块,若代码执行到这里,一定是有损坏的铁砧进行了修复
            //创造模式测试不消耗铁块
            if(!player.isCreative())
                player.getMainHandStack().setCount(count-1);
            //播放声音
            world.playSound(null,pos,SoundEvents.BLOCK_ANVIL_USE,BLOCKS,1f,1f);


            cir.setReturnValue(ActionResult.CONSUME);
        } else {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            player.incrementStat(Stats.INTERACT_WITH_ANVIL);
            cir.setReturnValue(ActionResult.CONSUME);
        }
    }
}
