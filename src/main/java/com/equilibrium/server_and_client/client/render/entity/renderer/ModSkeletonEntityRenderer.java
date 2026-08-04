package com.equilibrium.server_and_client.client.render.entity.renderer;

import com.equilibrium.entity.mob.ModAbstractSkeletonEntity;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModSkeletonEntityRenderer<T extends ModAbstractSkeletonEntity> extends HumanoidMobRenderer<T, SkeletonModel<T>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/skeleton/skeleton.png");

    public ModSkeletonEntityRenderer(EntityRendererProvider.Context context) {
        this(context, ModelLayers.SKELETON, ModelLayers.SKELETON_INNER_ARMOR, ModelLayers.SKELETON_OUTER_ARMOR);
    }

    public ModSkeletonEntityRenderer(EntityRendererProvider.Context ctx, ModelLayerLocation layer, ModelLayerLocation legArmorLayer, ModelLayerLocation bodyArmorLayer) {
        this(ctx, legArmorLayer, bodyArmorLayer, new SkeletonModel<>(ctx.bakeLayer(layer)));
    }

    public ModSkeletonEntityRenderer(
            EntityRendererProvider.Context context, ModelLayerLocation entityModelLayer, ModelLayerLocation entityModelLayer2, SkeletonModel<T> skeletonEntityModel
    ) {
        super(context, skeletonEntityModel, 0.5F);
        this.addLayer(
                new HumanoidArmorLayer<>(
                        this, new SkeletonModel(context.bakeLayer(entityModelLayer)), new SkeletonModel(context.bakeLayer(entityModelLayer2)), context.getModelManager()
                )
        );
    }

    public ResourceLocation getTextureLocation(T abstractSkeletonEntity) {
        return TEXTURE;
    }

    protected boolean isShaking(T abstractSkeletonEntity) {
        return abstractSkeletonEntity.isShaking();
    }
}
