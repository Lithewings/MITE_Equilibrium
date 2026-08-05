package com.equilibrium.mixin.render.entity_renderer;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.network.S2CIllnessTextureBooleanPacket;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Chicken;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChickenRenderer.class)
public class ChickenEntityRendererMixin {

    @Unique
    private static final ResourceLocation ILLNESS_TEXTURE = ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID,"textures/entity/sick_chicken.png");

    @Inject(method = "getTexture(Lnet/minecraft/entity/passive/ChickenEntity;)Lnet/minecraft/util/Identifier;",at=@At("HEAD"), cancellable = true)
    public void getTexture(Chicken chickenEntity, CallbackInfoReturnable<ResourceLocation> cir) {
        if(S2CIllnessTextureBooleanPacket.IllnessAppearancePayload.isIllness(chickenEntity.getId()))
            cir.setReturnValue(ILLNESS_TEXTURE);
    }
}
