package com.equilibrium.mixin.render.entity_renderer;

import com.equilibrium.MITEequilibrium;
import com.equilibrium.network.S2CIllnessTextureBooleanPacket;
import net.minecraft.client.render.entity.CowEntityRenderer;
import net.minecraft.client.render.entity.PigEntityRenderer;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PigEntityRenderer.class)
public class PigEntityRendererMixin {

    @Unique
    private static final Identifier ILLNESS_TEXTURE = Identifier.of(MITEequilibrium.MOD_ID,"textures/entity/sick_pig.png");

    @Inject(method = "getTexture(Lnet/minecraft/entity/passive/PigEntity;)Lnet/minecraft/util/Identifier;",at=@At("HEAD"), cancellable = true)
    public void getTexture(PigEntity pigEntity, CallbackInfoReturnable<Identifier> cir) {
        if(S2CIllnessTextureBooleanPacket.IllnessAppearancePayload.isIllness(pigEntity.getId()))
            cir.setReturnValue(ILLNESS_TEXTURE);
    }
}
