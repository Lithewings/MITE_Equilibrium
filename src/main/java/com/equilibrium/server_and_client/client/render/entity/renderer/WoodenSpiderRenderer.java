package com.equilibrium.server_and_client.client.render.entity.renderer;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.entity.mob.WoodenSpiderEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class WoodenSpiderRenderer extends SpiderRenderer {


    public WoodenSpiderRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID,"textures/entity/wooden_spider.png");

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(LivingEntity entity, PoseStack matrixStack, float amount) {
        matrixStack.scale(0.7F, 0.7F, 0.7F);
    }
}
