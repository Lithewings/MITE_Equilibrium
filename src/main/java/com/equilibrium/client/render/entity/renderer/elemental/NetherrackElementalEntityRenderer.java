package com.equilibrium.client.render.entity.renderer.elemental;

import com.equilibrium.client.render.entity.model.BaseEarthElementalEntityModel;
import com.equilibrium.client.render.entity.renderer.elemental.AbstractEarthElementalEntityRenderer;
import com.equilibrium.entity.mob.earth_elemental.NetherrackElementalEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class NetherrackElementalEntityRenderer extends AbstractEarthElementalEntityRenderer<NetherrackElementalEntity> {
    public NetherrackElementalEntityRenderer(EntityRendererFactory.Context ctx, BaseEarthElementalEntityModel<NetherrackElementalEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }
    public static final Identifier TEXTURE = Identifier.of(MOD_ID,"textures/entity/netherrack_elemental.png");
    @Override
    public Identifier getTexture(NetherrackElementalEntity entity) {
        return TEXTURE;
    }





}
