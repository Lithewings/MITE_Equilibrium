package com.equilibrium.server_and_client.client.render.entity.model;

import com.equilibrium.entity.mob.earth_elemental.AbstractEarthElementalEntity;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

public class BaseEarthElementalEntityModel<T extends AbstractEarthElementalEntity> extends HumanoidModel<T> {

    @Override
    public void setupAnim(T tTypeEntity, float f, float g, float h, float i, float j) {
        super.setupAnim(tTypeEntity, f, g, h, i, j);
        AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, this.isAttacking(tTypeEntity), this.attackTime, h);
    }

    public boolean isAttacking(T tTypeEntity) {
        return tTypeEntity.isAggressive();
    }


    public BaseEarthElementalEntityModel(ModelPart root) {
        super(root);
    }
}
