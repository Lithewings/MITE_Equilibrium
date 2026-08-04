package com.equilibrium.server_and_client.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.monster.Zombie;

@Environment(EnvType.CLIENT)
public class TransparentZombieEntityModelAbstractMobModel<T extends Zombie> extends TransparentAbstractMobModel<T> {
    public TransparentZombieEntityModelAbstractMobModel(ModelPart modelPart) {
        super(modelPart);
    }

    public boolean isAttacking(T zombieEntity) {
        return zombieEntity.isAggressive();
    }
}
