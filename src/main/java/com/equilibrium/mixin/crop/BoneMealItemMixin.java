package com.equilibrium.mixin.crop;

import com.equilibrium.event.MoonPhaseEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;


import static com.equilibrium.MITEequilibrium.CROP_IS_ILLNESS;
import static com.equilibrium.MITEequilibrium.FERTILIZED;
import static com.equilibrium.event.MoonPhaseEvent.CROP_BLOCK_POS;
import static net.minecraft.item.BoneMealItem.useOnFertilizable;
import static net.minecraft.item.BoneMealItem.useOnGround;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {

    @Shadow
    @Final
    public static int field_30851;

    @Inject(method = "useOnFertilizable", at = @At("HEAD"), cancellable = true)
    private static void useOnFertilizable1(ItemStack stack, World world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (world.getBlockState(pos).contains(CROP_IS_ILLNESS) && world.getBlockState(pos).get(CROP_IS_ILLNESS) && world instanceof ServerWorld serverWorld) {
            world.setBlockState(pos, world.getBlockState(pos).with(CROP_IS_ILLNESS, false));
            MoonPhaseEvent.CROP_BLOCK_POS.put(pos,false);
            MoonPhaseEvent.updateCropBlockPos(serverWorld);
            stack.decrement(1);
            cir.setReturnValue(true);
        } else if (world.random.nextInt(8) != 0) {
            cir.setReturnValue(true);
            stack.decrement(1);
        }
//        if(world.random.nextInt(8)!=0) {
//            cir.setReturnValue(true);
//            stack.decrement(1);
//        }
    }
//    @Inject(method = "useOnBlock",at= @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemUsageContext;getPlayer()Lnet/minecraft/entity/player/PlayerEntity;",ordinal = 0), cancellable = true)
//    private void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
//        World world = context.getWorld();
//        //考虑情景:骨粉选中农田上的农作物时,对其土地施肥也能生效
//        BlockPos blockPos = context.getBlockPos().down();
//        BlockState state = world.getBlockState(blockPos);
//        //注入的位置,stack数量-1的逻辑已经完成
//        if(state.contains(FERTILIZED) && state.get(FERTILIZED)==false){
//            world.setBlockState(blockPos, state.with(FERTILIZED, true), Block.NOTIFY_ALL);
//        }
//    }


}
