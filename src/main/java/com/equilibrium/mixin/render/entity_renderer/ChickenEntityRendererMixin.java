package com.equilibrium.mixin.render.entity_renderer;

import com.equilibrium.MITEequilibrium;
import com.equilibrium.network.S2CIllnessTextureBooleanPacket;
import net.minecraft.client.render.entity.ChickenEntityRenderer;
import net.minecraft.client.render.entity.CowEntityRenderer;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChickenEntityRenderer.class)
public class ChickenEntityRendererMixin {

    @Unique
    private static final Identifier ILLNESS_TEXTURE = Identifier.of(MITEequilibrium.MOD_ID,"textures/entity/sick_chicken.png");

    @Inject(method = "getTexture(Lnet/minecraft/entity/passive/ChickenEntity;)Lnet/minecraft/util/Identifier;",at=@At("HEAD"), cancellable = true)
    public void getTexture(ChickenEntity chickenEntity, CallbackInfoReturnable<Identifier> cir) {
        if(S2CIllnessTextureBooleanPacket.IllnessAppearancePayload.isIllness(chickenEntity.getId()))
            cir.setReturnValue(ILLNESS_TEXTURE);
    }
}
