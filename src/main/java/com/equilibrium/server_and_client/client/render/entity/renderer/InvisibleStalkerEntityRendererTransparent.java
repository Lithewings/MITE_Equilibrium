package com.equilibrium.server_and_client.client.render.entity.renderer;

import com.equilibrium.server_and_client.client.render.entity.model.TransparentZombieEntityModelAbstractMobModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class InvisibleStalkerEntityRendererTransparent extends AbstractTransparentZombieEntityRendererTransparent<Zombie, TransparentZombieEntityModelAbstractMobModel<Zombie>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID,"textures/entity/invisible_stalker.png");

    public ResourceLocation getTextureLocation(Zombie zombieEntity) {
        return TEXTURE;
    }

    public InvisibleStalkerEntityRendererTransparent(EntityRendererProvider.Context context) {
        this( context , ModelLayers.ZOMBIE, ModelLayers.ZOMBIE_INNER_ARMOR, ModelLayers.ZOMBIE_OUTER_ARMOR);
    }

    public InvisibleStalkerEntityRendererTransparent(EntityRendererProvider.Context ctx, ModelLayerLocation layer, ModelLayerLocation legsArmorLayer, ModelLayerLocation bodyArmorLayer) {
        super(
                ctx, new TransparentZombieEntityModelAbstractMobModel<>(ctx.bakeLayer(layer)),new TransparentZombieEntityModelAbstractMobModel<>(ctx.bakeLayer(layer)), new TransparentZombieEntityModelAbstractMobModel<>(ctx.bakeLayer(layer))
        );

    }
    //该生物不渲染碰撞箱:见EntityRenderDispatcherMixinForRenderHitBox
}

