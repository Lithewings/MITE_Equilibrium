package com.equilibrium.mixin.entitymixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MooshroomEntity.class)
public abstract class MooshroomEntityMixin  {

    @Inject(method = "<init>",at = @At("TAIL"))
    public void MooshroomEntity(EntityType entityType, World world, CallbackInfo ci) {
        MooshroomEntity mooshroomEntity = (MooshroomEntity) (Object) this;
        mooshroomEntity.discard();
    }
}
