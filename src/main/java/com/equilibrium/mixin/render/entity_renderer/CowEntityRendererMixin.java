package com.equilibrium.mixin.render.entity_renderer;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.network.S2CIllnessTextureBooleanPacket;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CowRenderer.class)
public class CowEntityRendererMixin {

    @Unique
    private static final ResourceLocation ILLNESS_TEXTURE = ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID,"textures/entity/sick_cow.png");

    @Inject(method = "getTexture(Lnet/minecraft/entity/passive/CowEntity;)Lnet/minecraft/util/Identifier;",at=@At("HEAD"), cancellable = true)
    public void getTexture(Cow cowEntity, CallbackInfoReturnable<ResourceLocation> cir) {
        if(S2CIllnessTextureBooleanPacket.IllnessAppearancePayload.isIllness(cowEntity.getId()))
            cir.setReturnValue(ILLNESS_TEXTURE);
    }
}
