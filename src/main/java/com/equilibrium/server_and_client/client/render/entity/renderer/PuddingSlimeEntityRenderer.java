package com.equilibrium.server_and_client.client.render.entity.renderer;

import com.equilibrium.entity.mob.BaseSlimeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class PuddingSlimeEntityRenderer extends BaseSlimeRenderer {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID,"textures/entity/pudding.png");
    public PuddingSlimeEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public ResourceLocation getTexture(BaseSlimeEntity baseSlimeEntity) {
        return TEXTURE;
    }

}
