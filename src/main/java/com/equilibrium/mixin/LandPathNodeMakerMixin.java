package com.equilibrium.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.swing.text.html.BlockView;

import static com.equilibrium.block.miscellaneous.MiscellaneousBlocks.BLUEBERRY_BUSH;


@Mixin(WalkNodeEvaluator.class)
public class LandPathNodeMakerMixin {
    @Inject(method = "getPathTypeFromState", at = @At("RETURN"), cancellable = true)
    private static void addBlueBerryBush(BlockGetter level, BlockPos pos, CallbackInfoReturnable<PathType> cir) {
        BlockState state = level.getBlockState(pos);
        if (state.is(BLUEBERRY_BUSH)) {
            cir.setReturnValue(PathType.DAMAGE_OTHER);
        }
    }
}