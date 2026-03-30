package com.equilibrium.server_and_client.client.render.entity.renderer;

import com.equilibrium.entity.mob.BaseSlimeEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class BaseSlimeRenderer extends MobEntityRenderer<BaseSlimeEntity, SlimeEntityModel<BaseSlimeEntity>> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/slime/slime.png");

    public BaseSlimeRenderer(EntityRendererFactory.Context context) {
        super(context, new SlimeEntityModel<>(context.getPart(EntityModelLayers.SLIME)), 0.25F);
        this.addFeature(new SlimeOverlayFeatureRenderer<>(this, context.getModelLoader()));
    }

    public void render(BaseSlimeEntity baseSlimeEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        this.shadowRadius = 0.25F * (float)baseSlimeEntity.getSize();
        super.render(baseSlimeEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    protected void scale(BaseSlimeEntity baseSlimeEntity, MatrixStack matrixStack, float f) {
        float g = 0.999F;
        matrixStack.scale(0.999F, 0.999F, 0.999F);
        matrixStack.translate(0.0F, 0.001F, 0.0F);
        float h = (float)baseSlimeEntity.getSize();
        float i = MathHelper.lerp(f, baseSlimeEntity.lastStretch, baseSlimeEntity.stretch) / (h * 0.5F + 1.0F);
        float j = 1.0F / (i + 1.0F);
        matrixStack.scale(j * h, 1.0F / j * h, j * h);
    }

    public Identifier getTexture(BaseSlimeEntity baseSlimeEntity) {
        return TEXTURE;
    }
}
