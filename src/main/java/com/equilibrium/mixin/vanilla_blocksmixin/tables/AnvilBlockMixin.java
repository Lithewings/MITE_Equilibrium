package com.equilibrium.mixin.vanilla_blocksmixin.tables;

import com.equilibrium.OnServerInitialize;
import net.minecraft.block.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static com.equilibrium.block.UseBlockActionUtil.isTableBlocked;
import static com.equilibrium.util.SharedConstant.ANVIL_DURABILITY;
import static net.minecraft.sound.SoundCategory.BLOCKS;

@Mixin(AnvilBlock.class)
public abstract class AnvilBlockMixin extends FallingBlock {
    public AnvilBlockMixin(Settings settings) {
        super(settings);
    }
    @Inject(method = "<init>",at = @At("TAIL"))
    public void AnvilBlock(Settings settings, CallbackInfo ci) {
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(ANVIL_DURABILITY,64));
    }





    @Inject(method = "appendProperties",at = @At("TAIL"))
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(ANVIL_DURABILITY);

    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if(world.isClient())
            return;

        int i = 64 - itemStack.getDamage();




        if(i>42 && i<=64){
            world.setBlockState(pos, Blocks.ANVIL.getDefaultState()
                    //copy facing
                    .with(AnvilBlock.FACING, state.get(AnvilBlock.FACING))
                    //copy damage level
                    .with(ANVIL_DURABILITY, i)
            );
        }
        else if(i<=42 && i>=22) {
            world.setBlockState(pos, Blocks.CHIPPED_ANVIL.getDefaultState()
                    //copy facing
                    .with(AnvilBlock.FACING, state.get(AnvilBlock.FACING))
                    //copy damage level
                    .with(ANVIL_DURABILITY, i)
            );
        }else if(i<22 && i>0){
            world.setBlockState(pos, Blocks.DAMAGED_ANVIL.getDefaultState()
                    //copy facing
                    .with(AnvilBlock.FACING, state.get(AnvilBlock.FACING))
                    //copy damage level
                    .with(ANVIL_DURABILITY, i)
            );
        } else if (i==0) {
            world.removeBlock(pos, false);
            world.syncWorldEvent(WorldEvents.ANVIL_DESTROYED, pos, 0);
        } else
            OnServerInitialize.LOGGER.error("\"ANVIL_DURABILITY\" can not be negative ");



    }

    @Override
    protected List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        ItemStack drop = super.getDroppedStacks(state, builder).getFirst();
        drop.set(DataComponentTypes.MAX_STACK_SIZE,1);
        drop.set(DataComponentTypes.MAX_DAMAGE,64);
        drop.set(DataComponentTypes.DAMAGE,64 - state.get(ANVIL_DURABILITY));
        return List.of(drop);
    }

    @Shadow
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;



    @Inject(method = "onUse",at = @At("HEAD"), cancellable = true)
    public void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        cir.cancel();
        if(isTableBlocked(world,pos,player)){
            cir.setReturnValue(ActionResult.PASS);
            return;
        }


        int i = state.get(ANVIL_DURABILITY);


        if (world.isClient) {
            cir.setReturnValue(ActionResult.SUCCESS);
        } else if (player.getMainHandStack().isOf(Items.IRON_BLOCK)) {
            BlockState newAnvilBlock = null;
            int count = player.getMainHandStack().getCount();
            //损坏的第一阶段—>完好无损
            if (state.getBlock() == Blocks.CHIPPED_ANVIL){
                newAnvilBlock = Blocks.ANVIL.getDefaultState()
                        .with(FACING, (Direction) state.get(FACING))
                        .with(ANVIL_DURABILITY,Math.clamp(i+24,0,64));
                player.playSound(SoundEvents.BLOCK_ANVIL_USE);
            }
            //损坏的第二阶段—>第一阶段
            else if(state.getBlock() == Blocks.DAMAGED_ANVIL){
                newAnvilBlock =Blocks.CHIPPED_ANVIL.getDefaultState()
                        .with(FACING, (Direction) state.get(FACING))
                        .with(ANVIL_DURABILITY,Math.clamp(i+24,0,64));
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
