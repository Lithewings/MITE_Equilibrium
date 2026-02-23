package com.equilibrium.mixin.vanilla_blocksmixin.tables;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static net.minecraft.sound.SoundCategory.BLOCKS;

@Mixin(AnvilBlock.class)
public abstract class AnvilBlockMixin extends FallingBlock {
    public AnvilBlockMixin(Settings settings) {
        super(settings);
    }
    @Shadow
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else if (player.getMainHandStack().isOf(Items.IRON_BLOCK)) {
            BlockState newAnvilBlock;
            int count = player.getMainHandStack().getCount();
            if (state.getBlock() == Blocks.CHIPPED_ANVIL){
                newAnvilBlock = Blocks.ANVIL.getDefaultState().with(FACING, (Direction) state.get(FACING));
                player.playSound(SoundEvents.BLOCK_ANVIL_USE);
            }else if(state.getBlock() == Blocks.DAMAGED_ANVIL){
                newAnvilBlock =Blocks.CHIPPED_ANVIL.getDefaultState().with(FACING, (Direction) state.get(FACING));
                player.playSound(SoundEvents.BLOCK_ANVIL_USE);
            }
            else {
                //如果是完好无损的铁砧,正常交互
                player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
                player.incrementStat(Stats.INTERACT_WITH_ANVIL);
                return ActionResult.CONSUME;
            }
            world.setBlockState(pos, newAnvilBlock);
            //消耗一个铁块,若代码执行到这里,一定是有损坏的铁砧进行了修复
            //创造模式测试不消耗铁块
            if(!player.isCreative())
                player.getMainHandStack().setCount(count-1);
            //播放声音
            world.playSound(null,pos,SoundEvents.BLOCK_ANVIL_USE,BLOCKS,1f,1f);


            return ActionResult.CONSUME;
        } else {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            player.incrementStat(Stats.INTERACT_WITH_ANVIL);
            return ActionResult.CONSUME;
        }
    }
}
