package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Cat.class)

public abstract class CatEntityMixin extends TamableAnimal implements VariantHolder<Holder<CatVariant>> {
    protected CatEntityMixin(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "customServerAiStep",at = @At("HEAD"))
    public void mobTick(CallbackInfo ci) {
        if(this.isTame())
            if(!this.hasEffect(MobEffects.DAMAGE_RESISTANCE))
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,-1,0,false,false,false));
        if(this.getHealth()<this.getMaxHealth() && this.isTame())
            if(!this.hasEffect(MobEffects.REGENERATION))
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION,10,3,false,false,false));
    }
}
