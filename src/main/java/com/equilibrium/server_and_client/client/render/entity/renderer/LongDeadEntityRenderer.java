package com.equilibrium.server_and_client.client.render.entity.renderer;

import com.equilibrium.entity.mob.LongDeadEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class LongDeadEntityRenderer extends ModSkeletonEntityRenderer<LongDeadEntity> {
    public LongDeadEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }
    private static final Identifier TEXTURE = Identifier.of(MOD_ID,"textures/entity/longdead.png");

    public Identifier getTexture(LongDeadEntity longDeadEntity) {
        return TEXTURE;
    }
}
