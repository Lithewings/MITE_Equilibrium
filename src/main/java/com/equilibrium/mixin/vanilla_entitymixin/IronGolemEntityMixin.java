package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.equilibrium.util.XpHashMap.getXpForLevel;

@Mixin(IronGolemEntity.class)
public abstract class IronGolemEntityMixin extends GolemEntity implements Angerable {
    protected IronGolemEntityMixin(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public int getXpToDrop(){
        return getXpForLevel(4);
    }
//
    @Inject(method = "createIronGolemAttributes",at = @At("HEAD"), cancellable = true)
    private static void createIronGolemAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.cancel();
        cir.setReturnValue(MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 19.0)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1.0)
                .add(EntityAttributes.GENERIC_ARMOR,8)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE,32)

        );
    }

    @Inject(method = "tryAttack",at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/IronGolemEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"))
    public void tryAttack(Entity target, CallbackInfoReturnable<Boolean> cir) {
        //64天之后,铁傀儡获得特殊强化
        if(this.getWorld().getTimeOfDay()/24000>=64)
            if (target instanceof HostileEntity hostileEntity && hostileEntity.isDead() ) {
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 2)); // 10秒再生
                if(this.hasStatusEffect(StatusEffects.REGENERATION))
                    this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 4));
            }
    }


    @Override
    public boolean isInAttackRange(LivingEntity entity) {
        return this.distanceTo(entity) <= 4;
    }




}
