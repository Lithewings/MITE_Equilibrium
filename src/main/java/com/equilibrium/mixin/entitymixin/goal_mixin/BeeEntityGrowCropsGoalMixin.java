package com.equilibrium.mixin.entitymixin.goal_mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(targets = "net.minecraft.entity.passive.BeeEntity$GrowCropsGoal")
public abstract class BeeEntityGrowCropsGoalMixin {
    @Unique
    Random random = new Random();
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(CallbackInfo ci) {
        if (!(this.random.nextInt(8) == 0)) {
            ci.cancel();
        }
    }
}
