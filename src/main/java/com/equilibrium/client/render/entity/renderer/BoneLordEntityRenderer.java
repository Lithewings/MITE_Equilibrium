package com.equilibrium.client.render.entity.renderer;

import com.equilibrium.entity.mob.BoneLordEntity;
import com.equilibrium.entity.mob.LongDeadEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class BoneLordEntityRenderer extends ModSkeletonEntityRenderer<BoneLordEntity> {
    public BoneLordEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.addFeature(new  BoneLordEntityRenderer.EyesFeatureRenderer(this));
    }
    private static final Identifier TEXTURE = Identifier.of(MOD_ID,"textures/entity/bone_lord.png");
    private static final Identifier TEXTURE_EYES = Identifier.of(MOD_ID,"textures/entity/bone_lord_glow.png");
    public Identifier getTexture(BoneLordEntity boneLordEntity) {
        return TEXTURE;
    }

    // 特征渲染器
    static class EyesFeatureRenderer extends FeatureRenderer<BoneLordEntity,SkeletonEntityModel<BoneLordEntity>> {
        public EyesFeatureRenderer(FeatureRendererContext<BoneLordEntity, SkeletonEntityModel<BoneLordEntity>> context) {
            super(context);
        }
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, BoneLordEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
            // 使用眼睛渲染层
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(
                    RenderLayer.getEyes(TEXTURE_EYES)
            );

            // 获取模型
            SkeletonEntityModel<BoneLordEntity> model = this.getContextModel();

            // 设置模型角度（重要！）
            model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);

            // 只渲染头部
            model.head.render(matrices, vertexConsumer, 15728640,
                    OverlayTexture.DEFAULT_UV);
        }
    }
}
