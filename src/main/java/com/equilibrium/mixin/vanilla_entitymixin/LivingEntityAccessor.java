package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("lastDamageStamp")
    long getPrivateLastDamageStamp();

    @Accessor("lastDamageStamp")
    void setPrivateLastDamageStamp(long stamp);

    @Accessor("lastDamageSource")
    @Nullable
    DamageSource getPrivateLastDamageSource();

    @Accessor("lastDamageSource")
    void setPrivateLastDamageSource(@Nullable DamageSource source);
}