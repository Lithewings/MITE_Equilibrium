package com.equilibrium.server_and_client.client.render.entity.renderer.elemental;

import com.equilibrium.server_and_client.client.render.entity.model.BaseEarthElementalEntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import com.equilibrium.entity.mob.earth_elemental.EndRockElementalEntity;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class EndRockElementalEntityRenderer extends AbstractEarthElementalEntityRenderer<EndRockElementalEntity> {
    public EndRockElementalEntityRenderer(EntityRendererProvider.Context ctx, BaseEarthElementalEntityModel<EndRockElementalEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID,"textures/entity/end_rock_elemental.png");
    @Override
    public ResourceLocation getTextureLocation(EndRockElementalEntity entity) {
        return TEXTURE;
    }





}
