package com.equilibrium.mixin.render.entity_renderer;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.network.S2CIllnessTextureBooleanPacket;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Pig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PigRenderer.class)
public class PigEntityRendererMixin {

    @Unique
    private static final ResourceLocation ILLNESS_TEXTURE = ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID,"textures/entity/sick_pig.png");

    @Inject(method = "getTexture(Lnet/minecraft/entity/passive/PigEntity;)Lnet/minecraft/util/Identifier;",at=@At("HEAD"), cancellable = true)
    public void getTexture(Pig pigEntity, CallbackInfoReturnable<ResourceLocation> cir) {
        if(S2CIllnessTextureBooleanPacket.IllnessAppearancePayload.isIllness(pigEntity.getId()))
            cir.setReturnValue(ILLNESS_TEXTURE);
    }
}
