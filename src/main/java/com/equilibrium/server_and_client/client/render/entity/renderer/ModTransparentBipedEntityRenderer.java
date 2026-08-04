package com.equilibrium.server_and_client.client.render.entity.renderer;

import com.equilibrium.server_and_client.client.render.entity.model.TransparentBipedEntityModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.Mob;

@Environment(EnvType.CLIENT)
public abstract class ModTransparentBipedEntityRenderer<T extends Mob, M extends TransparentBipedEntityModel<T>> extends MobRenderer<T, M> {
    public ModTransparentBipedEntityRenderer(EntityRendererProvider.Context ctx, M model, float shadowRadius) {
        this(ctx, model, shadowRadius, 1.0F, 1.0F, 1.0F);
    }

    public ModTransparentBipedEntityRenderer(EntityRendererProvider.Context ctx, M model, float shadowRadius, float scaleX, float scaleY, float scaleZ) {
        super(ctx, model, shadowRadius);
        this.addLayer(new CustomHeadLayer<>(this, ctx.getModelSet(), scaleX, scaleY, scaleZ, ctx.getItemInHandRenderer()));
        this.addLayer(new ElytraLayer<>(this, ctx.getModelSet()));
        this.addLayer(new ItemInHandLayer<>(this, ctx.getItemInHandRenderer()));
    }

}
