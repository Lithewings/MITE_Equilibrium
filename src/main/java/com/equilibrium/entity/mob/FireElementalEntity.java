package com.equilibrium.entity.mob;

import com.equilibrium.entity.goal.LavaPreference;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

import static com.equilibrium.entity.utilForEntity.forPlayerIsEnchantedItemCauseDamage;
import static com.equilibrium.util.XpHashMap.getXpForLevel;

public class FireElementalEntity extends HostileEntity {


    public FireElementalEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
        this.setPathfindingPenalty(PathNodeType.LAVA, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
        this.addStatusEffect(new StatusEffectInstance(
                StatusEffects.FIRE_RESISTANCE,  // 防火效果
                Integer.MAX_VALUE,              // 无限持续时间
                0,                              // 等级0
                false,                          // 不显示粒子
                false                           // 不显示图标
        ));
    }


    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        entityData =  super.initialize(world, difficulty, spawnReason, entityData);
        BlockPos pos =this.getBlockPos();
        while (world.getBlockState(pos).isAir() && pos.getY() > -64) {
            pos = pos.down();
        }
        this.setPosition(pos.getX(),pos.getY(),pos.getZ());
        return entityData;
    }

    @Override
    protected int getXpToDrop(){
        return getXpForLevel(1);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(3, new SwimGoal(this));
        this.goalSelector.add(4, new LavaPreference(this, 1.5));
        this.goalSelector.add(5, new GoToWalkTargetGoal(this, 1.0));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge());
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BLOCK_FIRE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BLOCK_FIRE_EXTINGUISH;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_FIRE_EXTINGUISH;
    }





    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
    }

    private SoundEvent getFallSound(int distance) {
        return distance > 4 ? this.getFallSounds().big() : this.getFallSounds().small();
    }

    @Override
    public void travel(Vec3d movementInput) {
        //源代码
        if (this.isLogicalSideForUpdatingMovement()) {
            double d = this.getFinalGravity();
            boolean bl = this.getVelocity().y <= 0.0;
            if (bl && this.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
                d = Math.min(d, 0.01);
            }

            FluidState fluidState = this.getWorld().getFluidState(this.getBlockPos());
            if (this.isTouchingWater() && this.shouldSwimInFluids() && !this.canWalkOnFluid(fluidState)) {
                double e = this.getY();
                float f = this.isSprinting() ? 0.9F : this.getBaseMovementSpeedMultiplier();
                float g = 0.02F;
                float h = (float)this.getAttributeValue(EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY);
                if (!this.isOnGround()) {
                    h *= 0.5F;
                }

                if (h > 0.0F) {
                    f += (0.54600006F - f) * h;
                    g += (this.getMovementSpeed() - g) * h;
                }

                if (this.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
                    f = 0.96F;
                }

                this.updateVelocity(g, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                Vec3d vec3d = this.getVelocity();
                if (this.horizontalCollision && this.isClimbing()) {
                    vec3d = new Vec3d(vec3d.x, 0.2, vec3d.z);
                }

                this.setVelocity(vec3d.multiply((double)f, 0.8F, (double)f));
                Vec3d vec3d2 = this.applyFluidMovingSpeed(d, bl, this.getVelocity());
                this.setVelocity(vec3d2);
                if (this.horizontalCollision && this.doesNotCollide(vec3d2.x, vec3d2.y + 0.6F - this.getY() + e, vec3d2.z)) {
                    this.setVelocity(vec3d2.x, 0.3F, vec3d2.z);
                }
            } else if (this.isInLava() && this.shouldSwimInFluids() && !this.canWalkOnFluid(fluidState)) {
                double ex = this.getY();



                //修改之处
                //——————————————————————————————
                this.updateVelocity(0.25F, movementInput);
                //——————————————————————————————





                this.move(MovementType.SELF, this.getVelocity());
                if (this.getFluidHeight(FluidTags.LAVA) <= this.getSwimHeight()) {
                    this.setVelocity(this.getVelocity().multiply(0.5, 0.8F, 0.5));
                    Vec3d vec3d3 = this.applyFluidMovingSpeed(d, bl, this.getVelocity());
                    this.setVelocity(vec3d3);
                } else {
                    this.setVelocity(this.getVelocity().multiply(0.5));
                }

                if (d != 0.0) {
                    this.setVelocity(this.getVelocity().add(0.0, -d / 4.0, 0.0));
                }

                Vec3d vec3d3 = this.getVelocity();
                if (this.horizontalCollision && this.doesNotCollide(vec3d3.x, vec3d3.y + 0.6F - this.getY() + ex, vec3d3.z)) {
                    this.setVelocity(vec3d3.x, 0.3F, vec3d3.z);
                }
            } else if (this.isFallFlying()) {
                this.limitFallDistance();
                Vec3d vec3d4 = this.getVelocity();
                Vec3d vec3d5 = this.getRotationVector();
                float fx = this.getPitch() * (float) (Math.PI / 180.0);
                double i = Math.sqrt(vec3d5.x * vec3d5.x + vec3d5.z * vec3d5.z);
                double j = vec3d4.horizontalLength();
                double k = vec3d5.length();
                double l = Math.cos((double)fx);
                l = l * l * Math.min(1.0, k / 0.4);
                vec3d4 = this.getVelocity().add(0.0, d * (-1.0 + l * 0.75), 0.0);
                if (vec3d4.y < 0.0 && i > 0.0) {
                    double m = vec3d4.y * -0.1 * l;
                    vec3d4 = vec3d4.add(vec3d5.x * m / i, m, vec3d5.z * m / i);
                }

                if (fx < 0.0F && i > 0.0) {
                    double m = j * (double)(-MathHelper.sin(fx)) * 0.04;
                    vec3d4 = vec3d4.add(-vec3d5.x * m / i, m * 3.2, -vec3d5.z * m / i);
                }

                if (i > 0.0) {
                    vec3d4 = vec3d4.add((vec3d5.x / i * j - vec3d4.x) * 0.1, 0.0, (vec3d5.z / i * j - vec3d4.z) * 0.1);
                }

                this.setVelocity(vec3d4.multiply(0.99F, 0.98F, 0.99F));
                this.move(MovementType.SELF, this.getVelocity());
                if (this.horizontalCollision && !this.getWorld().isClient) {
                    double m = this.getVelocity().horizontalLength();
                    double n = j - m;
                    float o = (float)(n * 10.0 - 3.0);
                    if (o > 0.0F) {
                        this.playSound(this.getFallSound((int)o), 1.0F, 1.0F);
                        this.damage(this.getDamageSources().flyIntoWall(), o);
                    }
                }

                if (this.isOnGround() && !this.getWorld().isClient) {
                    this.setFlag(Entity.FALL_FLYING_FLAG_INDEX, false);
                }
            } else {
                BlockPos blockPos = this.getVelocityAffectingPos();
                float p = this.getWorld().getBlockState(blockPos).getBlock().getSlipperiness();
                float fxx = this.isOnGround() ? p * 0.91F : 0.91F;
                Vec3d vec3d6 = this.applyMovementInput(movementInput, p);
                double q = vec3d6.y;
                if (this.hasStatusEffect(StatusEffects.LEVITATION)) {
                    q += (0.05 * (double)(this.getStatusEffect(StatusEffects.LEVITATION).getAmplifier() + 1) - vec3d6.y) * 0.2;
                } else if (!this.getWorld().isClient || this.getWorld().isChunkLoaded(blockPos)) {
                    q -= d;
                } else if (this.getY() > (double)this.getWorld().getBottomY()) {
                    q = -0.1;
                } else {
                    q = 0.0;
                }

                if (this.hasNoDrag()) {
                    this.setVelocity(vec3d6.x, q, vec3d6.z);
                } else {
                    this.setVelocity(vec3d6.x * (double)fxx, this instanceof Flutterer ? q * (double)fxx : q * 0.98F, vec3d6.z * (double)fxx);
                }
            }
        }

        this.updateLimbs(this instanceof Flutterer);
    }

    public static boolean canSpawn(EntityType<FireElementalEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return true;
    }


    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        //雪球一般可以正常造成伤害
        if(damageSource.getSource() instanceof SnowballEntity){
            return super.isInvulnerableTo(damageSource);
        }
        boolean b1 = forPlayerIsEnchantedItemCauseDamage(damageSource);
        //检查附魔
        if(!b1)
            return true;

        return super.isInvulnerableTo(damageSource);
    }

    int count = 0;

    @Override
    public boolean damage(DamageSource source, float amount) {
        if(source.getSource() instanceof SnowballEntity){
            return super.damage(this.getDamageSources().generic(), 10);
        }
        return super.damage(source, amount);
    }



    @Override
    public boolean hurtByWater() {
        return true;
    }

    @Override
    public void onAttacking(Entity target) {
        target.setOnFireFor(16);
        super.onAttacking(target);
    }

    @Override
    public void tick() {






        if (this.getWorld().getBlockState(this.getBlockPos()).isOf(Blocks.WATER)) {
            super.damage(this.getDamageSources().generic(),20);
        }
        count++;
        if (count > 20) {
            if (!this.getWorld().getBlockState(this.getBlockPos()).isOf(Blocks.LAVA)) {
                this.setHealth(this.getHealth() - 1);
            }
            else{
                this.heal(1);
            }
            if(this.isAlive())
                this.setFireTicks(24000);
            else{
                this.extinguish();
            }
            count=0;
        }
        super.tick();
    }
}
