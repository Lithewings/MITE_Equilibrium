package com.equilibrium.server_and_client.client.render.entity.renderer;

import static com.equilibrium.OnServerInitialize.MOD_ID;

import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public class RevenantEntityRenderer extends ZombieRenderer {
    //亡魂
    public RevenantEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.addLayer(new RevenantEyesFeatureRenderer(this));
    }
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID,"textures/entity/revenant.png");





    @Override
    public ResourceLocation getTextureLocation(Zombie zombieEntity) {
        return TEXTURE;
    }

    // 特征渲染器
    static class RevenantEyesFeatureRenderer extends EyesLayer<Zombie, ZombieModel<Zombie>> {

        private static final RenderType TEXTURE_EYES = RenderType.eyes((ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/entity/revenant_glow.png")));

        public RevenantEyesFeatureRenderer(RenderLayerParent<Zombie, ZombieModel<Zombie>> context) {
            super(context);
        }
        @Override
        public RenderType renderType() {
            return TEXTURE_EYES;
        }
    }
}
