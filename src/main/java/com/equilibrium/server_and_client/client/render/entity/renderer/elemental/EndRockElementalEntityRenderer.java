package com.equilibrium.server_and_client.client.render.entity.renderer.elemental;

import com.equilibrium.server_and_client.client.render.entity.model.BaseEarthElementalEntityModel;
import com.equilibrium.entity.mob.earth_elemental.EndRockElementalEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class EndRockElementalEntityRenderer extends AbstractEarthElementalEntityRenderer<EndRockElementalEntity> {
    public EndRockElementalEntityRenderer(EntityRendererFactory.Context ctx, BaseEarthElementalEntityModel<EndRockElementalEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }
    public static final Identifier TEXTURE = Identifier.of(MOD_ID,"textures/entity/end_rock_elemental.png");
    @Override
    public Identifier getTexture(EndRockElementalEntity entity) {
        return TEXTURE;
    }





}
