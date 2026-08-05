package com.equilibrium.mixin.render.entity_renderer;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.network.S2CIllnessTextureBooleanPacket;
import net.minecraft.client.renderer.entity.SheepRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SheepRenderer.class)
public class SheepEntityRendererMixin {
    @Unique
    private static final ResourceLocation ILLNESS_TEXTURE = ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID,"textures/entity/sick_sheep.png");

    @Inject(method = "getTexture(Lnet/minecraft/entity/passive/SheepEntity;)Lnet/minecraft/util/Identifier;",at=@At("HEAD"), cancellable = true)
    public void getTexture(Sheep sheepEntity, CallbackInfoReturnable<ResourceLocation> cir) {
        if(S2CIllnessTextureBooleanPacket.IllnessAppearancePayload.isIllness(sheepEntity.getId()))
            cir.setReturnValue(ILLNESS_TEXTURE);
    }
}
