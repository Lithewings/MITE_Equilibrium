package com.equilibrium.mixin.render.entity_renderer;

import com.equilibrium.MITEequilibrium;
import com.equilibrium.network.S2CCowIllnessTextureBooleanPacket;
import net.minecraft.client.render.entity.CowEntityRenderer;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CowEntityRenderer.class)
public class CowEntityRendererMixin {

    @Unique
    private static final Identifier ILLNESS_TEXTURE = Identifier.of(MITEequilibrium.MOD_ID,"textures/entity/sick_cow.png");

    @Inject(method = "getTexture(Lnet/minecraft/entity/passive/CowEntity;)Lnet/minecraft/util/Identifier;",at=@At("HEAD"), cancellable = true)
    public void getTexture(CowEntity cowEntity, CallbackInfoReturnable<Identifier> cir) {
        if(S2CCowIllnessTextureBooleanPacket.CowAppearancePayload.isIllness(cowEntity.getId()))
            cir.setReturnValue(ILLNESS_TEXTURE);
    }
}
