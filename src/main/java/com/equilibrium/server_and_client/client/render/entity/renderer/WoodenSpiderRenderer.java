package com.equilibrium.server_and_client.client.render.entity.renderer;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.entity.mob.WoodenSpiderEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SpiderEntityRenderer;
import net.minecraft.client.render.entity.feature.SpiderEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.util.Identifier;

public class WoodenSpiderRenderer extends SpiderEntityRenderer {

    public WoodenSpiderRenderer(EntityRendererFactory.Context context) {
        super(context, EntityModelLayers.CAVE_SPIDER);
        this.shadowRadius *= 0.7F;
    }
    private static final Identifier TEXTURE = Identifier.of(OnServerInitialize.MOD_ID,"textures/entity/wooden_spider.png");

    @Override
    public Identifier getTexture(Entity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(LivingEntity entity, MatrixStack matrixStack, float amount) {
        matrixStack.scale(0.7F, 0.7F, 0.7F);
    }
}
