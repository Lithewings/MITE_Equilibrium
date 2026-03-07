package com.equilibrium.mixin.some_special_rules;

import com.equilibrium.entity.mob.InvisibleStalkerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixinForRenderHitBox {
    @Inject(method = "renderHitbox",at = @At("HEAD"), cancellable = true)
    private static void renderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, float red, float green, float blue, CallbackInfo ci) {
        //取消对隐形潜伏者的碰撞箱渲染
        if(entity instanceof InvisibleStalkerEntity)
            ci.cancel();
    }
}
