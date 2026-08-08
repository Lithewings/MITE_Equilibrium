package com.equilibrium.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.equilibrium.block.ModBlocksRegistry.BLUE_BERRY_BUSH;

@Mixin(LandPathNodeMaker.class)
public class LandPathNodeMakerMixin {

    @Inject(method = "getCommonNodeType", at = @At("RETURN"), cancellable = true)
    private static void addBlueBerryBush(BlockView world, BlockPos pos, CallbackInfoReturnable<PathNodeType> cir) {
        BlockState state = world.getBlockState(pos);
        if (state.isOf(BLUE_BERRY_BUSH)) {
            cir.setReturnValue(PathNodeType.DAMAGE_OTHER);
        }
    }
}