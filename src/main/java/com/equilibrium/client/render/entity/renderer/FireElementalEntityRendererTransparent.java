package com.equilibrium.client.render.entity.renderer;

import com.equilibrium.client.render.entity.model.FireElementEntityModel;
import com.equilibrium.client.render.entity.model.TransparentBipedEntityModel;
import com.equilibrium.entity.mob.FireElementalEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BlazeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class FireElementalEntityRendererTransparent extends ModTransparentBipedEntityRenderer{


    private static final Identifier TEXTURE = Identifier.of(MOD_ID,"textures/entity/fire_elemental.png");

    public FireElementalEntityRendererTransparent(EntityRendererFactory.Context context) {
        //借用僵尸模型
        super(context ,new FireElementEntityModel(context.getPart(EntityModelLayers.ZOMBIE)),0f);
    }

    @Override
    public Identifier getTexture(Entity entity) {
        return TEXTURE;
    }


}
