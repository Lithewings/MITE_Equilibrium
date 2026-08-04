package com.equilibrium.server_and_client.client.render.entity.renderer;

import com.equilibrium.entity.mob.LongDeadEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class LongDeadEntityRenderer extends ModSkeletonEntityRenderer<LongDeadEntity> {
    public LongDeadEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID,"textures/entity/longdead.png");

    public ResourceLocation getTexture(LongDeadEntity longDeadEntity) {
        return TEXTURE;
    }
}
