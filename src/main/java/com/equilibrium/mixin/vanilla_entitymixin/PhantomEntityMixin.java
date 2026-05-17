package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import static com.equilibrium.util.XpHashMap.getXpForLevel;
@Mixin(PhantomEntity.class )
public abstract class PhantomEntityMixin extends FlyingEntity implements Monster {

    protected PhantomEntityMixin(EntityType<? extends FlyingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected int getXpToDrop() {
        return getXpForLevel(3);
    }

}
