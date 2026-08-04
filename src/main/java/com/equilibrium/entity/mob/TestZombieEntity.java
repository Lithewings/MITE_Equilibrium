package com.equilibrium.entity.mob;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class TestZombieEntity extends Zombie {
    public TestZombieEntity(EntityType<? extends TestZombieEntity> entityType, Level world) {
        super(entityType, world);
    }

}


