package com.equilibrium.mixin.structure_and_dimension.strong_hold;

import net.minecraft.data.worldgen.StructureSets;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ConcentricRingsStructurePlacement.class)
public abstract class ConcentricRingsStructurePlacementMixin implements StructureSets {
    @Inject(method = "distance",at = @At("RETURN"), cancellable = true)
    public void getDistance(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(16);
    }
    @Inject(method = "spread",at = @At("RETURN"), cancellable = true)
    public void getSpread(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(1);
    }
    @Inject(method = "count",at = @At("RETURN"), cancellable = true)
    public void getCount(CallbackInfoReturnable<Integer> cir) {
        //谨慎选取大小,这会严重影响加载世界时的性能
        cir.setReturnValue(128);
    }
}
