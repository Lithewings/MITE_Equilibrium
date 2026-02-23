package com.equilibrium.mixin.vanilla_entitymixin.goal_mixin;

import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BowAttackGoal.class)
public abstract class BowAttackGoalMixin <T extends HostileEntity & RangedAttackMob> extends Goal {
    @Shadow
    @Final
    private  T actor;
    @Shadow
    @Final
    private double speed;
    @Shadow
    private int attackInterval;
    @Shadow
    @Final
    private  float squaredRange;
    @Shadow
    private int cooldown = -1;
    @Shadow
    private int targetSeeingTicker;
    @Shadow
    private boolean movingToLeft;
    @Shadow
    private boolean backward;
    @Shadow
    private int combatTicks = -1;




//    @Override
//    public void tick() {
//        LivingEntity livingEntity = this.actor.getTarget();
//        if (livingEntity != null) {
//            double d = this.actor.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
//            boolean bl = this.actor.getVisibilityCache().canSee(livingEntity);
//            boolean bl2 = this.targetSeeingTicker > 0;
//            if (bl != bl2) {
//                this.targetSeeingTicker = 0;
//            }
//
//            if (bl) {
//                this.targetSeeingTicker++;
//            } else {
//                this.targetSeeingTicker--;
//            }
//            // 修改点1：降低视线检测阈值
//            if (!(d > (double)this.squaredRange) && this.targetSeeingTicker >= 1) {
//                this.actor.getNavigation().stop();
//                this.combatTicks++;
//            } else {
//                this.actor.getNavigation().startMovingTo(livingEntity, this.speed);
//                this.combatTicks = -1;
//            }
//
//            if (this.combatTicks >= 20) {
//                if ((double)this.actor.getRandom().nextFloat() < 0.3) {
//                    this.movingToLeft = !this.movingToLeft;
//                }
//
//                if ((double)this.actor.getRandom().nextFloat() < 0.3) {
//                    this.backward = !this.backward;
//                }
//
//                this.combatTicks = 0;
//            }
//
//            if (this.combatTicks > -1) {
//                if (d > (double)(this.squaredRange * 0.75F)) {
//                    this.backward = false;
//                } else if (d < (double)(this.squaredRange * 0.25F)) {
//                    this.backward = true;
//                }
//
//                this.actor.getMoveControl().strafeTo(this.backward ? -0.5F : 0.5F, this.movingToLeft ? 0.5F : -0.5F);
//                if (this.actor.getControllingVehicle() instanceof MobEntity mobEntity) {
//                    mobEntity.lookAtEntity(livingEntity, 45.0F, 45.0F);
//                }
//
//                this.actor.lookAtEntity(livingEntity,  45.0F, 45.0F);
//            } else {
//                this.actor.getLookControl().lookAt(livingEntity,  45.0F, 45.0F);
//            }
//
//            if (this.actor.isUsingItem()) {
//                if (!bl && this.targetSeeingTicker < -60) {
//                    this.actor.clearActiveItem();
//                } else if (bl) {
//                    int i = this.actor.getItemUseTime();
//                    if (i >= 20) {
//                        this.actor.clearActiveItem();
//                        this.actor.shootAt(livingEntity, BowItem.getPullProgress(i));
//                        this.cooldown = this.attackInterval;
//                    }
//                }
//            } else if (--this.cooldown <= 0 && this.targetSeeingTicker >= -60) {
//                this.actor.setCurrentHand(ProjectileUtil.getHandPossiblyHolding(this.actor, Items.BOW));
//            }
//        }
//    }
}
