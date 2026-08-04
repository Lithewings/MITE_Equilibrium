package com.equilibrium.server_and_client.client.render.entity.renderer.elemental;

import com.equilibrium.server_and_client.client.render.entity.model.BaseEarthElementalEntityModel;
import com.equilibrium.server_and_client.client.render.entity.renderer.elemental.AbstractEarthElementalEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import com.equilibrium.entity.mob.earth_elemental.ObsidianElementalEntity;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class ObsidianElementalEntityRenderer extends AbstractEarthElementalEntityRenderer<ObsidianElementalEntity> {
    public ObsidianElementalEntityRenderer(EntityRendererProvider.Context ctx, BaseEarthElementalEntityModel<ObsidianElementalEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID,"textures/entity/obsidian_elemental.png");
    @Override
    public ResourceLocation getTextureLocation(ObsidianElementalEntity entity) {
        return TEXTURE;
    }





}
