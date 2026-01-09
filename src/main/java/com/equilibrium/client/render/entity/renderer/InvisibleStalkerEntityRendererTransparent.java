package com.equilibrium.client.render.entity.renderer;

import com.equilibrium.client.render.entity.model.TransparentZombieEntityModelAbstractMobModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class InvisibleStalkerEntityRendererTransparent extends AbstractTransparentZombieEntityRendererTransparent<ZombieEntity, TransparentZombieEntityModelAbstractMobModel<ZombieEntity>> {
    private static final Identifier TEXTURE = Identifier.of(MOD_ID,"textures/entity/invisible_stalker.png");

    public Identifier getTexture(ZombieEntity zombieEntity) {
        return TEXTURE;
    }

    public InvisibleStalkerEntityRendererTransparent(EntityRendererFactory.Context context) {
        this( context , EntityModelLayers.ZOMBIE, EntityModelLayers.ZOMBIE_INNER_ARMOR, EntityModelLayers.ZOMBIE_OUTER_ARMOR);
    }

    public InvisibleStalkerEntityRendererTransparent(EntityRendererFactory.Context ctx, EntityModelLayer layer, EntityModelLayer legsArmorLayer, EntityModelLayer bodyArmorLayer) {
        super(
                ctx, new TransparentZombieEntityModelAbstractMobModel<>(ctx.getPart(layer)),new TransparentZombieEntityModelAbstractMobModel<>(ctx.getPart(layer)), new TransparentZombieEntityModelAbstractMobModel<>(ctx.getPart(layer))
        );

    }
    //该生物不渲染碰撞箱:见EntityRenderDispatcherMixinForRenderHitBox
}

