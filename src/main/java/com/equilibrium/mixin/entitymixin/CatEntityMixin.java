package com.equilibrium.mixin.entitymixin;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CatEntity.class)

public abstract class CatEntityMixin extends TameableEntity implements VariantHolder<RegistryEntry<CatVariant>> {
    protected CatEntityMixin(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "mobTick",at = @At("HEAD"))
    public void mobTick(CallbackInfo ci) {
        if(this.isTamed())
            if(!this.hasStatusEffect(StatusEffects.RESISTANCE))
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,-1,0,false,false,false));
        if(this.getHealth()<this.getMaxHealth() && this.isTamed())
            if(!this.hasStatusEffect(StatusEffects.REGENERATION))
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION,10,3,false,false,false));
    }
}
