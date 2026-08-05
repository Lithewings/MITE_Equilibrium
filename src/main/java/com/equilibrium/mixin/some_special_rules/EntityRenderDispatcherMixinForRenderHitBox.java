package com.equilibrium.mixin.some_special_rules;

import com.equilibrium.entity.mob.InvisibleStalkerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixinForRenderHitBox {
    @Inject(method = "renderHitbox",at = @At("HEAD"), cancellable = true)
    private static void renderHitbox(PoseStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, float red, float green, float blue, CallbackInfo ci) {
        //取消对隐形潜伏者的碰撞箱渲染
        if(entity instanceof InvisibleStalkerEntity)
            ci.cancel();
    }
}
