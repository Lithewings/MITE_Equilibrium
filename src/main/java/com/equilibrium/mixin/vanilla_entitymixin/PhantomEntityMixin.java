package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

import static com.equilibrium.util.XpHashMap.getXpForLevel;
@Mixin(Phantom.class )
public abstract class PhantomEntityMixin extends FlyingMob implements Enemy {

    protected PhantomEntityMixin(EntityType<? extends FlyingMob> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected int getBaseExperienceReward() {
        return getXpForLevel(3);
    }

}
