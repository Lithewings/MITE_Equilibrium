package com.equilibrium.mixin.vanilla_entitymixin.goal_mixin;

import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.Goal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EscapeDangerGoal.class)
public abstract class EscapeDangerGoalMixin extends Goal {
    @Shadow
    @Final
    protected double speed = 2;
}
