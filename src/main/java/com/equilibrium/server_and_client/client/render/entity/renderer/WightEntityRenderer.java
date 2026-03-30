package com.equilibrium.server_and_client.client.render.entity.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class WightEntityRenderer extends ZombieEntityRenderer {
    //影子潜伏者(在最黑的位置生成,破坏火把)
    public WightEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }
    private static final Identifier TEXTURE = Identifier.of(MOD_ID,"textures/entity/wight.png");

    @Override
    public Identifier getTexture(ZombieEntity zombieEntity) {
        return TEXTURE;
    }
}
