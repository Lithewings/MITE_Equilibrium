package com.equilibrium.server_and_client.client.render.entity.renderer;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class RevenantEntityRenderer extends ZombieEntityRenderer {
    //亡魂
    public RevenantEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.addFeature(new RevenantEyesFeatureRenderer(this));
    }
    private static final Identifier TEXTURE = Identifier.of(MOD_ID,"textures/entity/revenant.png");





    @Override
    public Identifier getTexture(ZombieEntity zombieEntity) {
        return TEXTURE;
    }

    // 特征渲染器
    static class RevenantEyesFeatureRenderer extends EyesFeatureRenderer<ZombieEntity, ZombieEntityModel<ZombieEntity>> {

        private static final RenderLayer TEXTURE_EYES = RenderLayer.getEyes((Identifier.of(MOD_ID, "textures/entity/revenant_glow.png")));

        public RevenantEyesFeatureRenderer(FeatureRendererContext<ZombieEntity, ZombieEntityModel<ZombieEntity>> context) {
            super(context);
        }
        @Override
        public RenderLayer getEyesTexture() {
            return TEXTURE_EYES;
        }
    }
}
