package com.equilibrium.client.render.entity.model;

import com.equilibrium.entity.mob.earth_elemental.AbstractEarthElementalEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.CrossbowPosing;

public class BaseEarthElementalEntityModel<T extends AbstractEarthElementalEntity> extends BipedEntityModel<T> {

    @Override
    public void setAngles(T tTypeEntity, float f, float g, float h, float i, float j) {
        super.setAngles(tTypeEntity, f, g, h, i, j);
        CrossbowPosing.meleeAttack(this.leftArm, this.rightArm, this.isAttacking(tTypeEntity), this.handSwingProgress, h);
    }

    public boolean isAttacking(T tTypeEntity) {
        return tTypeEntity.isAttacking();
    }


    public BaseEarthElementalEntityModel(ModelPart root) {
        super(root);
    }
}
