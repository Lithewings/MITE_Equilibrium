package com.equilibrium.mixin.some_special_rules;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TripWireHookBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TripWireHookBlock.class)
public class BanThreadMachine {
    @Redirect(method = "calculateState", at = @At(value = "INVOKE", target ="Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private static boolean checkBeforeSet(Level world, BlockPos pos, BlockState state, int flags){
        if(world.getDayTime()/24000L>=64)
            return false;
        if (world.getBlockState(pos).is(state.getBlock())){
//            LOGGER.info("Should Check BlockState");
            return world.setBlock(pos,state, flags);
        }else {
            return false;
        }
    }
}
