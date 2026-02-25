package com.equilibrium.client.render.entity.renderer.elemental;

import com.equilibrium.client.render.entity.model.BaseEarthElementalEntityModel;
import com.equilibrium.entity.mob.earth_elemental.AbstractEarthElementalEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public abstract class AbstractEarthElementalEntityRenderer<T extends AbstractEarthElementalEntity> extends BipedEntityRenderer<T, BaseEarthElementalEntityModel<T>> {


    public AbstractEarthElementalEntityRenderer(EntityRendererFactory.Context ctx, BaseEarthElementalEntityModel<T> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
        this.addFeature(new BaseEarthElementalEyesRenderer<>(this));
    }


     static class BaseEarthElementalEyesRenderer<T extends AbstractEarthElementalEntity> extends EyesFeatureRenderer<T, BaseEarthElementalEntityModel<T>> {

        private static final RenderLayer TEXTURE_EYES = RenderLayer.getEyes((Identifier.of(MOD_ID, "textures/entity/stone_elemental_glow.png")));

        public BaseEarthElementalEyesRenderer(FeatureRendererContext<T,BaseEarthElementalEntityModel<T>> featureRendererContext) {
            super(featureRendererContext);
        }

        @Override
        public RenderLayer getEyesTexture() {
            return TEXTURE_EYES;
        }

    }



}
