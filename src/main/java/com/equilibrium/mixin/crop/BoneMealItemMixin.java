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
import static com.equilibrium.event.CropIllnessEvent.CROP_BLOCK_POS;
import static com.equilibrium.event.CropIllnessEvent.updateCropBlockPos;


@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {

    @Shadow
    @Final
    public static int field_30851;

    @Inject(method = "useOnFertilizable", at = @At("HEAD"), cancellable = true)
    private static void useOnFertilizable1(ItemStack stack, World world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (world.getBlockState(pos).contains(CROP_IS_ILLNESS) && world.getBlockState(pos).get(CROP_IS_ILLNESS) && world instanceof ServerWorld serverWorld) {
            world.setBlockState(pos, world.getBlockState(pos).with(CROP_IS_ILLNESS, false));
            CROP_BLOCK_POS.put(pos,false);
            updateCropBlockPos(serverWorld);
            stack.decrement(1);
            cir.setReturnValue(true);
        } else if (world.random.nextInt(8) != 0) {
            cir.setReturnValue(true);
            stack.decrement(1);
        }
    }



}
