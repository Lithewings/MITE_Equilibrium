package com.equilibrium.mixin.structure_and_dimension.strong_hold;

import net.minecraft.structure.StructureSets;
import net.minecraft.world.gen.chunk.placement.ConcentricRingsStructurePlacement;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ConcentricRingsStructurePlacement.class)
public abstract class ConcentricRingsStructurePlacementMixin implements StructureSets {
    @Inject(method = "getDistance",at = @At("RETURN"), cancellable = true)
    public void getDistance(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(16);
    }
    @Inject(method = "getSpread",at = @At("RETURN"), cancellable = true)
    public void getSpread(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(1);
    }
    @Inject(method = "getCount",at = @At("RETURN"), cancellable = true)
    public void getCount(CallbackInfoReturnable<Integer> cir) {
        //谨慎选取大小,这会严重影响加载世界时的性能
        cir.setReturnValue(128);
    }
}
