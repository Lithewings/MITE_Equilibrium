package com.equilibrium.server_and_client.client.render.entity.renderer;

import com.equilibrium.entity.mob.ModAbstractSkeletonEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModSkeletonEntityRenderer<T extends ModAbstractSkeletonEntity> extends BipedEntityRenderer<T, SkeletonEntityModel<T>> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/skeleton/skeleton.png");

    public ModSkeletonEntityRenderer(EntityRendererFactory.Context context) {
        this(context, EntityModelLayers.SKELETON, EntityModelLayers.SKELETON_INNER_ARMOR, EntityModelLayers.SKELETON_OUTER_ARMOR);
    }

    public ModSkeletonEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer, EntityModelLayer legArmorLayer, EntityModelLayer bodyArmorLayer) {
        this(ctx, legArmorLayer, bodyArmorLayer, new SkeletonEntityModel<>(ctx.getPart(layer)));
    }

    public ModSkeletonEntityRenderer(
            EntityRendererFactory.Context context, EntityModelLayer entityModelLayer, EntityModelLayer entityModelLayer2, SkeletonEntityModel<T> skeletonEntityModel
    ) {
        super(context, skeletonEntityModel, 0.5F);
        this.addFeature(
                new ArmorFeatureRenderer<>(
                        this, new SkeletonEntityModel(context.getPart(entityModelLayer)), new SkeletonEntityModel(context.getPart(entityModelLayer2)), context.getModelManager()
                )
        );
    }

    public Identifier getTexture(T abstractSkeletonEntity) {
        return TEXTURE;
    }

    protected boolean isShaking(T abstractSkeletonEntity) {
        return abstractSkeletonEntity.isShaking();
    }
}
