package com.equilibrium.server_and_client.client.render.entity.renderer;

import com.equilibrium.server_and_client.client.render.entity.model.TransparentZombieEntityModelAbstractMobModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

@Environment(EnvType.CLIENT)
public class TestTransparentZombieRendererTransparent extends AbstractTransparentZombieEntityRendererTransparent<Zombie, TransparentZombieEntityModelAbstractMobModel<Zombie>> {


    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/player/wide/steve.png");
    public ResourceLocation getTextureLocation(Zombie zombieEntity) {
        return TEXTURE;
    }
    public TestTransparentZombieRendererTransparent(EntityRendererProvider.Context context) {
        this(context, ModelLayers.ZOMBIE, ModelLayers.ZOMBIE_INNER_ARMOR, ModelLayers.ZOMBIE_OUTER_ARMOR);

    }
    public TestTransparentZombieRendererTransparent(EntityRendererProvider.Context ctx, ModelLayerLocation layer, ModelLayerLocation legsArmorLayer, ModelLayerLocation bodyArmorLayer) {
        super(
                ctx, new TransparentZombieEntityModelAbstractMobModel<>(ctx.bakeLayer(layer)), new TransparentZombieEntityModelAbstractMobModel<>(ctx.bakeLayer(layer)), new TransparentZombieEntityModelAbstractMobModel<>(ctx.bakeLayer(layer))
        );
    }

}
