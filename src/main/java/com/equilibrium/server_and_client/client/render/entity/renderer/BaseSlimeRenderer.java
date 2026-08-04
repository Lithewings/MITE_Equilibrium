package com.equilibrium.server_and_client.client.render.entity.renderer;

import com.equilibrium.entity.mob.BaseSlimeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BaseSlimeRenderer extends MobRenderer<BaseSlimeEntity, SlimeModel<BaseSlimeEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/slime/slime.png");

    public BaseSlimeRenderer(EntityRendererProvider.Context context) {
        super(context, new SlimeModel<>(context.bakeLayer(ModelLayers.SLIME)), 0.25F);
        this.addLayer(new SlimeOuterLayer<>(this, context.getModelSet()));
    }

    public void render(BaseSlimeEntity baseSlimeEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        this.shadowRadius = 0.25F * (float)baseSlimeEntity.getSize();
        super.render(baseSlimeEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    protected void scale(BaseSlimeEntity baseSlimeEntity, PoseStack matrixStack, float f) {
        float g = 0.999F;
        matrixStack.scale(0.999F, 0.999F, 0.999F);
        matrixStack.translate(0.0F, 0.001F, 0.0F);
        float h = (float)baseSlimeEntity.getSize();
        float i = Mth.lerp(f, baseSlimeEntity.lastStretch, baseSlimeEntity.stretch) / (h * 0.5F + 1.0F);
        float j = 1.0F / (i + 1.0F);
        matrixStack.scale(j * h, 1.0F / j * h, j * h);
    }

    public ResourceLocation getTextureLocation(BaseSlimeEntity baseSlimeEntity) {
        return TEXTURE;
    }
}
