package com.equilibrium.server_and_client.client.render.entity.renderer;

import static com.equilibrium.OnServerInitialize.MOD_ID;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public class ShadowEntityRenderer extends ZombieRenderer {
    //影子潜伏者(在最黑的位置生成,破坏火把)
    public ShadowEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID,"textures/entity/shadow.png");

    @Override
    public ResourceLocation getTextureLocation(Zombie zombieEntity) {
        return TEXTURE;
    }
}
