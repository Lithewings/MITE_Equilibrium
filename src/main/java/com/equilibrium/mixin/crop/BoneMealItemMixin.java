package com.equilibrium.mixin.crop;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import static com.equilibrium.OnServerInitialize.CROP_IS_ILLNESS;
import static com.equilibrium.server_and_client.server.event.CropIllnessEvent.CROP_BLOCK_POS;
import static com.equilibrium.server_and_client.server.event.CropIllnessEvent.updateCropBlockPos;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;


@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {


    @Inject(method = "applyBonemeal", at = @At("HEAD"), cancellable = true)
    private static void useOnFertilizable1(ItemStack stack, Level world, BlockPos pos, Player player, CallbackInfoReturnable<Boolean> cir) {
        if (world.getBlockState(pos).hasProperty(CROP_IS_ILLNESS) && world.getBlockState(pos).getValue(CROP_IS_ILLNESS) && world instanceof ServerLevel serverWorld) {
            world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(CROP_IS_ILLNESS, false));
            CROP_BLOCK_POS.put(pos,false);
            updateCropBlockPos(serverWorld);
            stack.shrink(1);
            cir.setReturnValue(true);
        } else if(world.getBlockState(pos).is(Blocks.GRASS_BLOCK)) {
            return;
        }
        else if (world.random.nextInt(8) != 0) {
            cir.setReturnValue(true);
            stack.shrink(1);
        }

    }



}
