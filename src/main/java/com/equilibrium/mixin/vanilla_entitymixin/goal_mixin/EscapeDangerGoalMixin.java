package com.equilibrium.mixin.vanilla_entitymixin.goal_mixin;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PanicGoal.class)
public abstract class EscapeDangerGoalMixin extends Goal {

}
