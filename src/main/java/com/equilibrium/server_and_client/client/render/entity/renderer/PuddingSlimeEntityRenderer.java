package com.equilibrium.server_and_client.client.render.entity.renderer;

import com.equilibrium.entity.mob.BaseSlimeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class PuddingSlimeEntityRenderer extends BaseSlimeRenderer {

    private static final Identifier TEXTURE = Identifier.of(MOD_ID,"textures/entity/pudding.png");
    public PuddingSlimeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    public Identifier getTexture(BaseSlimeEntity baseSlimeEntity) {
        return TEXTURE;
    }

}
