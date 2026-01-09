package com.equilibrium.mixin.render.entity_renderer;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.network.S2CIllnessTextureBooleanPacket;
import net.minecraft.client.render.entity.SheepEntityRenderer;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SheepEntityRenderer.class)
public class SheepEntityRendererMixin {
    @Unique
    private static final Identifier ILLNESS_TEXTURE = Identifier.of(OnServerInitialize.MOD_ID,"textures/entity/sick_sheep.png");

    @Inject(method = "getTexture(Lnet/minecraft/entity/passive/SheepEntity;)Lnet/minecraft/util/Identifier;",at=@At("HEAD"), cancellable = true)
    public void getTexture(SheepEntity sheepEntity, CallbackInfoReturnable<Identifier> cir) {
        if(S2CIllnessTextureBooleanPacket.IllnessAppearancePayload.isIllness(sheepEntity.getId()))
            cir.setReturnValue(ILLNESS_TEXTURE);
    }
}
