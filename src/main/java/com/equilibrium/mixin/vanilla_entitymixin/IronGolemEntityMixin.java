package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

import static com.equilibrium.util.XpHashMap.getXpForLevel;

@Mixin(IronGolem.class)
public abstract class IronGolemEntityMixin extends AbstractGolem implements NeutralMob {
    protected IronGolemEntityMixin(EntityType<? extends AbstractGolem> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public int getBaseExperienceReward(){
        return getXpForLevel(4);
    }

    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity entity) {
        return this.distanceTo(entity) <= 4;
    }




}
