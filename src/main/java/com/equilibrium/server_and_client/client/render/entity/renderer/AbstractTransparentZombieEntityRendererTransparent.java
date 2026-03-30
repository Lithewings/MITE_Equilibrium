package com.equilibrium.server_and_client.client.render.entity.renderer;

import com.equilibrium.server_and_client.client.render.entity.model.TransparentZombieEntityModelAbstractMobModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.mob.ZombieEntity;

@Environment(EnvType.CLIENT)
public abstract class AbstractTransparentZombieEntityRendererTransparent<T extends ZombieEntity, M extends TransparentZombieEntityModelAbstractMobModel<T>> extends ModTransparentBipedEntityRenderer<T, M> {


    protected AbstractTransparentZombieEntityRendererTransparent(EntityRendererFactory.Context ctx, M bodyModel, M legsArmorModel, M bodyArmorModel) {
        super(ctx, bodyModel, 0.5F);

    }


    protected boolean isShaking(T zombieEntity) {
        return super.isShaking(zombieEntity) || zombieEntity.isConvertingInWater();
    }
}
