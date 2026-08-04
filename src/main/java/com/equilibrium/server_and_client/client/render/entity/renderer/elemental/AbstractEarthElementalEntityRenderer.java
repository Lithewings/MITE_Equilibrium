package com.equilibrium.server_and_client.client.render.entity.renderer.elemental;

import com.equilibrium.server_and_client.client.render.entity.model.BaseEarthElementalEntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import com.equilibrium.entity.mob.earth_elemental.AbstractEarthElementalEntity;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public abstract class AbstractEarthElementalEntityRenderer<T extends AbstractEarthElementalEntity> extends HumanoidMobRenderer<T, BaseEarthElementalEntityModel<T>> {


    public AbstractEarthElementalEntityRenderer(EntityRendererProvider.Context ctx, BaseEarthElementalEntityModel<T> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
        this.addLayer(new BaseEarthElementalEyesRenderer<>(this));
    }


     static class BaseEarthElementalEyesRenderer<T extends AbstractEarthElementalEntity> extends EyesLayer<T, BaseEarthElementalEntityModel<T>> {

        private static final RenderType TEXTURE_EYES = RenderType.eyes((ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/entity/stone_elemental_glow.png")));

        public BaseEarthElementalEyesRenderer(RenderLayerParent<T,BaseEarthElementalEntityModel<T>> featureRendererContext) {
            super(featureRendererContext);
        }

        @Override
        public RenderType renderType() {
            return TEXTURE_EYES;
        }

    }



}
