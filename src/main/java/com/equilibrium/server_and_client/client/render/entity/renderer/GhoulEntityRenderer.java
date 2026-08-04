package com.equilibrium.server_and_client.client.render.entity.renderer;

import static com.equilibrium.OnServerInitialize.MOD_ID;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public class GhoulEntityRenderer extends ZombieRenderer {
    //食尸鬼
    public GhoulEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID,"textures/entity/ghoul.png");

    @Override
    public ResourceLocation getTextureLocation(Zombie zombieEntity) {
        return TEXTURE;
    }
}
