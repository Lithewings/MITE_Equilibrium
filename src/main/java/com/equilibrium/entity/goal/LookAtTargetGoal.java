package com.equilibrium.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class LookAtTargetGoal extends Goal {
    private final Mob mob;
    private LivingEntity target;

    public LookAtTargetGoal(Mob mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        this.target = this.mob.getTarget();
        return this.target != null && this.target.isAlive();
    }

    @Override
    public void tick() {
        if (this.target != null) {
            this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        }
    }
}
