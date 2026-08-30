package com.equilibrium.server_and_client.client.render.entity.renderer;

import com.equilibrium.entity.mob.BoneLordEntity;
import com.equilibrium.entity.mob.LongDeadEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class BoneLordEntityRenderer extends ModSkeletonEntityRenderer<BoneLordEntity> {
    public BoneLordEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.addLayer(new  BoneLordEntityRenderer.EyesFeatureRenderer(this));
    }
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID,"textures/entity/bone_lord.png");
    private static final ResourceLocation TEXTURE_EYES = ResourceLocation.fromNamespaceAndPath(MOD_ID,"textures/entity/bone_lord_glow.png");
    public ResourceLocation getTextureLocation(BoneLordEntity boneLordEntity) {
        return TEXTURE;
    }

    // 特征渲染器
    static class EyesFeatureRenderer extends RenderLayer<BoneLordEntity,SkeletonModel<BoneLordEntity>> {
        public EyesFeatureRenderer(RenderLayerParent<BoneLordEntity, SkeletonModel<BoneLordEntity>> context) {
            super(context);
        }
        @Override
        public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, BoneLordEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
            // 使用眼睛渲染层
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(
                    RenderType.eyes(TEXTURE_EYES)
            );

            // 获取模型
            SkeletonModel<BoneLordEntity> model = this.getParentModel();

            // 设置模型角度（重要！）
            model.setupAnim(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);

            // 只渲染头部
            model.head.render(matrices, vertexConsumer, 15728640,
                    OverlayTexture.NO_OVERLAY);
        }
    }
}
