package com.equilibrium.entity.mob;

import com.equilibrium.entity.goal.LavaPreference;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import static com.equilibrium.entity.utilForEntity.forPlayerIsEnchantedItemCauseDamage;
import static com.equilibrium.util.XpHashMap.getXpForLevel;

public class FireElementalEntity extends Monster {


    public FireElementalEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.LAVA, 0.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
        this.addEffect(new MobEffectInstance(
                MobEffects.FIRE_RESISTANCE,  // 防火效果
                Integer.MAX_VALUE,              // 无限持续时间
                0,                              // 等级0
                false,                          // 不显示粒子
                false                           // 不显示图标
        ));
    }


    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData) {
        entityData =  super.finalizeSpawn(world, difficulty, spawnReason, entityData);
        BlockPos pos =this.blockPosition();
        while (world.getBlockState(pos).isAir() && pos.getY() > -64) {
            pos = pos.below();
        }
        this.setPos(pos.getX(),pos.getY(),pos.getZ());
        return entityData;
    }

    @Override
    protected int getBaseExperienceReward(){
        return getXpForLevel(1);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(3, new FloatGoal(this));
        this.goalSelector.addGoal(4, new LavaPreference(this, 1.5));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.FIRE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.FIRE_EXTINGUISH;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.FIRE_EXTINGUISH;
    }





    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
    }

    private SoundEvent getFallDamageSound(int distance) {
        return distance > 4 ? this.getFallSounds().big() : this.getFallSounds().small();
    }

    @Override
    public void travel(Vec3 movementInput) {
        //源代码
        if (this.isControlledByLocalInstance()) {
            double d = this.getGravity();
            boolean bl = this.getDeltaMovement().y <= 0.0;
            if (bl && this.hasEffect(MobEffects.SLOW_FALLING)) {
                d = Math.min(d, 0.01);
            }

            FluidState fluidState = this.level().getFluidState(this.blockPosition());
            if (this.isInWater() && this.isAffectedByFluids() && !this.canStandOnFluid(fluidState)) {
                double e = this.getY();
                float f = this.isSprinting() ? 0.9F : this.getWaterSlowDown();
                float g = 0.02F;
                float h = (float)this.getAttributeValue(Attributes.WATER_MOVEMENT_EFFICIENCY);
                if (!this.onGround()) {
                    h *= 0.5F;
                }

                if (h > 0.0F) {
                    f += (0.54600006F - f) * h;
                    g += (this.getSpeed() - g) * h;
                }

                if (this.hasEffect(MobEffects.DOLPHINS_GRACE)) {
                    f = 0.96F;
                }

                this.moveRelative(g, movementInput);
                this.move(MoverType.SELF, this.getDeltaMovement());
                Vec3 vec3d = this.getDeltaMovement();
                if (this.horizontalCollision && this.onClimbable()) {
                    vec3d = new Vec3(vec3d.x, 0.2, vec3d.z);
                }

                this.setDeltaMovement(vec3d.multiply((double)f, 0.8F, (double)f));
                Vec3 vec3d2 = this.getFluidFallingAdjustedMovement(d, bl, this.getDeltaMovement());
                this.setDeltaMovement(vec3d2);
                if (this.horizontalCollision && this.isFree(vec3d2.x, vec3d2.y + 0.6F - this.getY() + e, vec3d2.z)) {
                    this.setDeltaMovement(vec3d2.x, 0.3F, vec3d2.z);
                }
            } else if (this.isInLava() && this.isAffectedByFluids() && !this.canStandOnFluid(fluidState)) {
                double ex = this.getY();



                //修改之处
                //——————————————————————————————
                this.moveRelative(0.25F, movementInput);
                //——————————————————————————————





                this.move(MoverType.SELF, this.getDeltaMovement());
                if (this.getFluidHeight(FluidTags.LAVA) <= this.getFluidJumpThreshold()) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 0.8F, 0.5));
                    Vec3 vec3d3 = this.getFluidFallingAdjustedMovement(d, bl, this.getDeltaMovement());
                    this.setDeltaMovement(vec3d3);
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
                }

                if (d != 0.0) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0, -d / 4.0, 0.0));
                }

                Vec3 vec3d3 = this.getDeltaMovement();
                if (this.horizontalCollision && this.isFree(vec3d3.x, vec3d3.y + 0.6F - this.getY() + ex, vec3d3.z)) {
                    this.setDeltaMovement(vec3d3.x, 0.3F, vec3d3.z);
                }
            } else if (this.isFallFlying()) {
                this.checkSlowFallDistance();
                Vec3 vec3d4 = this.getDeltaMovement();
                Vec3 vec3d5 = this.getLookAngle();
                float fx = this.getXRot() * (float) (Math.PI / 180.0);
                double i = Math.sqrt(vec3d5.x * vec3d5.x + vec3d5.z * vec3d5.z);
                double j = vec3d4.horizontalDistance();
                double k = vec3d5.length();
                double l = Math.cos((double)fx);
                l = l * l * Math.min(1.0, k / 0.4);
                vec3d4 = this.getDeltaMovement().add(0.0, d * (-1.0 + l * 0.75), 0.0);
                if (vec3d4.y < 0.0 && i > 0.0) {
                    double m = vec3d4.y * -0.1 * l;
                    vec3d4 = vec3d4.add(vec3d5.x * m / i, m, vec3d5.z * m / i);
                }

                if (fx < 0.0F && i > 0.0) {
                    double m = j * (double)(-Mth.sin(fx)) * 0.04;
                    vec3d4 = vec3d4.add(-vec3d5.x * m / i, m * 3.2, -vec3d5.z * m / i);
                }

                if (i > 0.0) {
                    vec3d4 = vec3d4.add((vec3d5.x / i * j - vec3d4.x) * 0.1, 0.0, (vec3d5.z / i * j - vec3d4.z) * 0.1);
                }

                this.setDeltaMovement(vec3d4.multiply(0.99F, 0.98F, 0.99F));
                this.move(MoverType.SELF, this.getDeltaMovement());
                if (this.horizontalCollision && !this.level().isClientSide) {
                    double m = this.getDeltaMovement().horizontalDistance();
                    double n = j - m;
                    float o = (float)(n * 10.0 - 3.0);
                    if (o > 0.0F) {
                        this.playSound(this.getFallDamageSound((int)o), 1.0F, 1.0F);
                        this.hurt(this.damageSources().flyIntoWall(), o);
                    }
                }

                if (this.onGround() && !this.level().isClientSide) {
                    this.setSharedFlag(Entity.FLAG_FALL_FLYING, false);
                }
            } else {
                BlockPos blockPos = this.getBlockPosBelowThatAffectsMyMovement();
                float p = this.level().getBlockState(blockPos).getBlock().getFriction();
                float fxx = this.onGround() ? p * 0.91F : 0.91F;
                Vec3 vec3d6 = this.handleRelativeFrictionAndCalculateMovement(movementInput, p);
                double q = vec3d6.y;
                if (this.hasEffect(MobEffects.LEVITATION)) {
                    q += (0.05 * (double)(this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1) - vec3d6.y) * 0.2;
                } else if (!this.level().isClientSide || this.level().hasChunkAt(blockPos)) {
                    q -= d;
                } else if (this.getY() > (double)this.level().getMinBuildHeight()) {
                    q = -0.1;
                } else {
                    q = 0.0;
                }

                if (this.shouldDiscardFriction()) {
                    this.setDeltaMovement(vec3d6.x, q, vec3d6.z);
                } else {
                    this.setDeltaMovement(vec3d6.x * (double)fxx, this instanceof FlyingAnimal ? q * (double)fxx : q * 0.98F, vec3d6.z * (double)fxx);
                }
            }
        }

        this.calculateEntityAnimation(this instanceof FlyingAnimal);
    }

    public static boolean canSpawn(EntityType<FireElementalEntity> type, LevelAccessor world, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        return true;
    }


    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return damageSource.getEntity() instanceof Player
                ? !forPlayerIsEnchantedItemCauseDamage(damageSource)
                : super.isInvulnerableTo(damageSource);
    }

    int count = 0;

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if(source.getDirectEntity() instanceof Snowball){
            return super.hurt(this.damageSources().generic(), 10);
        }
        return super.hurt(source, amount);
    }



    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    public void setLastHurtMob(Entity target) {
        target.igniteForSeconds(16);
        super.setLastHurtMob(target);
    }

    @Override
    public void tick() {






        if (this.level().getBlockState(this.blockPosition()).is(Blocks.WATER)) {
            super.hurt(this.damageSources().generic(),20);
        }
        count++;
        if (count > 20) {
            if (!this.level().getBlockState(this.blockPosition()).is(Blocks.LAVA)) {
                this.setHealth(this.getHealth() - 1);
            }
            else{
                this.heal(1);
            }
            if(this.isAlive())
                this.setRemainingFireTicks(24000);
            else{
                this.clearFire();
            }
            count=0;
        }
        super.tick();
    }
}
