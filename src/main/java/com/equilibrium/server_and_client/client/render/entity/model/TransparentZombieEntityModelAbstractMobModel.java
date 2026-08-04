package com.equilibrium.server_and_client.client.render.entity.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.monster.Zombie;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TransparentZombieEntityModelAbstractMobModel<T extends Zombie> extends TransparentAbstractMobModel<T> {
    public TransparentZombieEntityModelAbstractMobModel(ModelPart modelPart) {
        super(modelPart);
    }

    public boolean isAttacking(T zombieEntity) {
        return zombieEntity.isAggressive();
    }
}
