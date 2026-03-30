package com.equilibrium.server_and_client.client.render.entity.renderer.elemental;

import com.equilibrium.server_and_client.client.render.entity.model.BaseEarthElementalEntityModel;
import com.equilibrium.server_and_client.client.render.entity.renderer.elemental.AbstractEarthElementalEntityRenderer;
import com.equilibrium.entity.mob.earth_elemental.ObsidianElementalEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class ObsidianElementalEntityRenderer extends AbstractEarthElementalEntityRenderer<ObsidianElementalEntity> {
    public ObsidianElementalEntityRenderer(EntityRendererFactory.Context ctx, BaseEarthElementalEntityModel<ObsidianElementalEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }
    public static final Identifier TEXTURE = Identifier.of(MOD_ID,"textures/entity/obsidian_elemental.png");
    @Override
    public Identifier getTexture(ObsidianElementalEntity entity) {
        return TEXTURE;
    }





}
