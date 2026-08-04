package com.equilibrium.server_and_client.client.render.entity.renderer;

import com.equilibrium.server_and_client.client.render.entity.model.TransparentZombieEntityModelAbstractMobModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.monster.Zombie;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractTransparentZombieEntityRendererTransparent<T extends Zombie, M extends TransparentZombieEntityModelAbstractMobModel<T>> extends ModTransparentBipedEntityRenderer<T, M> {


    protected AbstractTransparentZombieEntityRendererTransparent(EntityRendererProvider.Context ctx, M bodyModel, M legsArmorModel, M bodyArmorModel) {
        super(ctx, bodyModel, 0.5F);

    }


    protected boolean isShaking(T zombieEntity) {
        return super.isShaking(zombieEntity) || zombieEntity.isUnderWaterConverting();
    }
}
