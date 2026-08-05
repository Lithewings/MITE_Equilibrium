package com.equilibrium.mixin.crop;

import com.equilibrium.item.food.FoodItems;
import com.equilibrium.item.material.MaterialItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.equilibrium.OnServerInitialize.FERTILIZED;


@Mixin(FarmBlock.class)
public abstract class FarmlandBlockMixin extends Block {
    @Shadow @Final public static IntegerProperty MOISTURE;

    public FarmlandBlockMixin(Properties settings) {
        super(settings);
    }


    @Inject(method = "createBlockStateDefinition",at = @At(value = "TAIL"))
    protected void appendProperties(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(FERTILIZED);
    }

//    @Override
//    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
//        builder.add(MOISTURE);
//    }
//
//
//    private boolean FERTILIZED =false;
////
////
////    @Override
////    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
////        builder.add(FERTILIZED);
////    }
//
//
    @Inject(method = "<init>",at = @At(value = "TAIL"))
    public void FarmlandBlock(Properties settings, CallbackInfo ci) {
        this.registerDefaultState(this.stateDefinition.any().setValue(FERTILIZED, false));
    }
//
//
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (itemStack.is(FoodItems.MANURE)) {
            if (!world.isClientSide) {
                // 设置施肥状态为 true

                world.setBlock(pos, state.setValue(FERTILIZED, true), Block.UPDATE_ALL);
                // 添加视觉和声音效果
                world.levelEvent(1505, pos, 0); // 骨粉使用效果

                // 消耗肥料（非创造模式）
                if (!player.getAbilities().instabuild) {
                    player.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }







//        if(state.get(FERTILIZED)==true){
//            player.sendMessage(Text.of("该耕地已被施肥"));
//            return ActionResult.PASS;
//        }
//        else if(state.get(FERTILIZED)==false){
//            player.sendMessage(Text.of("该耕地还没有被施肥"));
//            return ActionResult.PASS;
//        }






        return InteractionResult.PASS;
    }

}
