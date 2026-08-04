package com.equilibrium.server_and_client.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.monster.Monster;

@Environment(EnvType.CLIENT)
public abstract class TransparentAbstractMobModel<T extends Monster> extends TransparentBipedEntityModel<T> {
    protected TransparentAbstractMobModel(ModelPart modelPart) {
        super(modelPart);
    }

    public void setupAnim(T hostileEntity, float f, float g, float h, float i, float j) {
        super.setupAnim(hostileEntity, f, g, h, i, j);
        AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, this.isAttacking(hostileEntity), this.attackTime, h);
    }

    public abstract boolean isAttacking(T entity);
}
