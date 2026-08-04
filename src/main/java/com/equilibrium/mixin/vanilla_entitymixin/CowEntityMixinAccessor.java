package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface CowEntityMixinAccessor {
    @Accessor("lastDamageStamp")
    long getLastDamageStamp();

    @Accessor("lastDamageStamp")
    void setLastDamageStamp(long stamp);

    @Accessor("lastDamageSource")
    @Nullable
    DamageSource getLastDamageSource();

    @Accessor("lastDamageSource")
    void setLastDamageSource(@Nullable DamageSource source);
}