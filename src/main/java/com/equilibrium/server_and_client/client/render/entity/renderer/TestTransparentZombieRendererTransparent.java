package com.equilibrium.server_and_client.client.render.entity.renderer;

import com.equilibrium.server_and_client.client.render.entity.model.TransparentZombieEntityModelAbstractMobModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TestTransparentZombieRendererTransparent extends AbstractTransparentZombieEntityRendererTransparent<ZombieEntity, TransparentZombieEntityModelAbstractMobModel<ZombieEntity>> {


    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/player/wide/steve.png");
    public Identifier getTexture(ZombieEntity zombieEntity) {
        return TEXTURE;
    }
    public TestTransparentZombieRendererTransparent(EntityRendererFactory.Context context) {
        this(context, EntityModelLayers.ZOMBIE, EntityModelLayers.ZOMBIE_INNER_ARMOR, EntityModelLayers.ZOMBIE_OUTER_ARMOR);

    }
    public TestTransparentZombieRendererTransparent(EntityRendererFactory.Context ctx, EntityModelLayer layer, EntityModelLayer legsArmorLayer, EntityModelLayer bodyArmorLayer) {
        super(
                ctx, new TransparentZombieEntityModelAbstractMobModel<>(ctx.getPart(layer)), new TransparentZombieEntityModelAbstractMobModel<>(ctx.getPart(layer)), new TransparentZombieEntityModelAbstractMobModel<>(ctx.getPart(layer))
        );
    }

}
