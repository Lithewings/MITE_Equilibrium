package com.equilibrium.server_and_client.client.render.entity.renderer;

import com.equilibrium.server_and_client.client.render.entity.model.FireElementEntityModel;
import com.equilibrium.server_and_client.client.render.entity.model.TransparentBipedEntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import com.equilibrium.entity.mob.FireElementalEntity;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class FireElementalEntityRendererTransparent extends ModTransparentBipedEntityRenderer{


    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID,"textures/entity/fire_elemental.png");

    public FireElementalEntityRendererTransparent(EntityRendererProvider.Context context) {
        //借用僵尸模型
        super(context ,new FireElementEntityModel(context.bakeLayer(ModelLayers.ZOMBIE)),0f);
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        return TEXTURE;
    }


}
