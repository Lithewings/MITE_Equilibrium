package com.equilibrium.client.render.entity.renderer.elemental;

import com.equilibrium.client.render.entity.model.BaseEarthElementalEntityModel;
import com.equilibrium.client.render.entity.renderer.elemental.AbstractEarthElementalEntityRenderer;
import com.equilibrium.entity.mob.earth_elemental.StoneElementalEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class StoneElementalEntityRenderer extends AbstractEarthElementalEntityRenderer<StoneElementalEntity> {
    public StoneElementalEntityRenderer(EntityRendererFactory.Context ctx, BaseEarthElementalEntityModel<StoneElementalEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }
    public static final Identifier TEXTURE = Identifier.of(MOD_ID,"textures/entity/stone_elemental.png");
    @Override
    public Identifier getTexture(StoneElementalEntity entity) {
        return TEXTURE;
    }





}
