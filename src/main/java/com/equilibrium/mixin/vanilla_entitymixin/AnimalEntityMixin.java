package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Animal.class)
public abstract class AnimalEntityMixin extends AgeableMob {
    protected AnimalEntityMixin(EntityType<? extends AgeableMob> entityType, Level world) {
        super(entityType, world);
    }
    @Override
    public int getBaseExperienceReward() {
        return 0;
    }
}
