package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MushroomCow.class)
public abstract class MooshroomEntityMixin  {

    @Inject(method = "<init>",at = @At("TAIL"))
    public void MooshroomEntity(EntityType entityType, Level world, CallbackInfo ci) {
        MushroomCow mooshroomEntity = (MushroomCow) (Object) this;
        mooshroomEntity.discard();
    }
}
