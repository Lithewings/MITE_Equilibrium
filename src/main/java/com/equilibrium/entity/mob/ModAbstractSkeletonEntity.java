package com.equilibrium.entity.mob;

import com.equilibrium.entity.goal.MeleeAttackGoalApplyAttackRange;
import com.equilibrium.tags.ModItemTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.EnumSet;

public abstract class ModAbstractSkeletonEntity extends HostileEntity implements RangedAttackMob {
    private static final int HARD_ATTACK_INTERVAL = 20;
    private static final int REGULAR_ATTACK_INTERVAL = 40;
    private final BowAttackGoal<ModAbstractSkeletonEntity> bowAttackGoal = new BowAttackGoal<>(this, 1.0, 20, 15.0F);
    private final MeleeAttackGoalApplyAttackRange meleeAttackGoal = new MeleeAttackGoalApplyAttackRange(this, 1.2, true,1.5f) {
        @Override
        public void stop() {
            super.stop();
            ModAbstractSkeletonEntity.this.setAttacking(false);
        }

        @Override
        public void start() {
            super.start();
            ModAbstractSkeletonEntity.this.setAttacking(true);
        }

        @Override
        public float getAttackRange() {
            return this.mob.getMainHandStack().isIn(ModItemTags.SWORDS)?3f:1.5f;
        }

    };

    protected ModAbstractSkeletonEntity(EntityType<? extends ModAbstractSkeletonEntity> entityType, World world) {
        super(entityType, world);
        this.updateAttackType();
    }



    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new AvoidSunlightGoal(this));
        this.goalSelector.add(3, new EscapeSunlightGoal(this, 1.0));
        this.goalSelector.add(3, new FleeEntityGoal(this, WolfEntity.class, 6.0F, 1.0, 1.2));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
    }

    public static DefaultAttributeContainer.Builder createAbstractSkeletonAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    abstract SoundEvent getStepSound();

    @Override
    public void tickMovement() {
        boolean bl = this.isAffectedByDaylight();
        if (bl) {
            ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
            if (!itemStack.isEmpty()) {
                if (itemStack.isDamageable()) {
                    Item item = itemStack.getItem();
                    itemStack.setDamage(itemStack.getDamage() + this.random.nextInt(2));
                    if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
                        this.sendEquipmentBreakStatus(item, EquipmentSlot.HEAD);
                        this.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                    }
                }

                bl = false;
            }

            if (bl) {
                this.setOnFireFor(8.0F);
            }
        }

        super.tickMovement();
    }

    @Override
    public void tickRiding() {
        super.tickRiding();
        if (this.getControllingVehicle() instanceof PathAwareEntity pathAwareEntity) {
            this.bodyYaw = pathAwareEntity.bodyYaw;
        }
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        super.initEquipment(random, localDifficulty);
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData);
        Random random = world.getRandom();
        this.initEquipment(random, difficulty);
        this.updateEnchantments(world, random, difficulty);
        this.updateAttackType();
        this.setCanPickUpLoot(random.nextFloat() < 0.55F * difficulty.getClampedLocalDifficulty());
        if (this.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
            LocalDate localDate = LocalDate.now();
            int i = localDate.get(ChronoField.DAY_OF_MONTH);
            int j = localDate.get(ChronoField.MONTH_OF_YEAR);
            if (j == 10 && i == 31 && random.nextFloat() < 0.25F) {
                this.equipStack(EquipmentSlot.HEAD, new ItemStack(random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.armorDropChances[EquipmentSlot.HEAD.getEntitySlotId()] = 0.0F;
            }
        }

        return entityData;
    }

    public void updateAttackType() {
        if (this.getWorld() != null && !this.getWorld().isClient) {
            this.goalSelector.remove(this.meleeAttackGoal);
            this.goalSelector.remove(this.bowAttackGoal);
            ItemStack itemStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW));
            if (itemStack.isOf(Items.BOW)) {
                int i = this.getHardAttackInterval();
                if (this.getWorld().getDifficulty() != Difficulty.HARD) {
                    i = this.getRegularAttackInterval();
                }

                this.bowAttackGoal.setAttackInterval(i);
                this.goalSelector.add(4, this.bowAttackGoal);
            } else {
                this.goalSelector.add(4, this.meleeAttackGoal);
            }
        }
    }

    protected int getHardAttackInterval() {
        return 20;
    }

    protected int getRegularAttackInterval() {
        return 40;
    }

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        ItemStack itemStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW));
        ItemStack itemStack2 = this.getProjectileType(itemStack);
        PersistentProjectileEntity persistentProjectileEntity = this.createArrowProjectile(itemStack2, pullProgress, itemStack);
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333) - persistentProjectileEntity.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        persistentProjectileEntity.setVelocity(d, e + g * 0.2F, f, 1.6F, (float)(14 - this.getWorld().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.getWorld().spawnEntity(persistentProjectileEntity);
    }

    protected PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier, @Nullable ItemStack shotFrom) {
        return ProjectileUtil.createArrowProjectile(this, arrow, damageModifier, shotFrom);
    }

    @Override
    public boolean canUseRangedWeapon(RangedWeaponItem weapon) {
        return weapon == Items.BOW;
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.updateAttackType();
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        super.equipStack(slot, stack);
        if (!this.getWorld().isClient) {
            this.updateAttackType();
        }
    }

    public boolean isShaking() {
        return this.isFrozen();
    }



    class BowAttackGoal<T extends HostileEntity & RangedAttackMob> extends Goal {
        private final T actor;
        private final double speed;
        private int attackInterval;
        private final float squaredRange;
        private int cooldown = -1;
        private int targetSeeingTicker;
        private boolean movingToLeft;
        private boolean backward;
        private int combatTicks = -1;

        public BowAttackGoal(T actor, double speed, int attackInterval, float range) {
            this.actor = actor;
            this.speed = speed;
            this.attackInterval = attackInterval;
            this.squaredRange = range * range;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        public void setAttackInterval(int attackInterval) {
            this.attackInterval = attackInterval;
        }

        @Override
        public boolean canStart() {
            return this.actor.getTarget() == null ? false : this.isHoldingBow();
        }

        protected boolean isHoldingBow() {
            return this.actor.isHolding(Items.BOW);
        }

        @Override
        public boolean shouldContinue() {
            return (this.canStart() || !this.actor.getNavigation().isIdle()) && this.isHoldingBow();
        }

        @Override
        public void start() {
            super.start();
            this.actor.setAttacking(true);
        }

        @Override
        public void stop() {
            super.stop();
            this.actor.setAttacking(false);
            this.targetSeeingTicker = 0;
            this.cooldown = -1;
            this.actor.clearActiveItem();
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = this.actor.getTarget();
            if (livingEntity != null) {
                double d = this.actor.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                boolean canSeeTarget = this.actor.getVisibilityCache().canSee(livingEntity);
                boolean wasSeeingTarget = this.targetSeeingTicker > 0;

                if (canSeeTarget != wasSeeingTarget) {
                    this.targetSeeingTicker = 0;
                }

                if (canSeeTarget) {
                    this.targetSeeingTicker++;
                } else {
                    this.targetSeeingTicker--;
                }

                // 如果看不到目标，使用导航移动到目标位置
                if (!canSeeTarget) {
                    this.actor.getNavigation().startMovingTo(livingEntity, this.speed);
                    this.combatTicks = -1;
                } else {
                    // 能看到目标，根据距离决定行为
                    if (d < (double)(this.squaredRange * 0.25F)) {
                        // 距离太近：一边向后撤退一边射箭
                        this.actor.getNavigation().stop(); // 停止导航移动

                        // 向后移动拉开距离
                        this.actor.getMoveControl().strafeTo(-1F, 0);

                        // 保持战斗状态，允许射箭
                        if (this.targetSeeingTicker >= 10) { // 降低阈值，更快进入射击状态
                            this.combatTicks = Math.max(this.combatTicks, 0);
                        }

                        // 始终看向目标
                        this.actor.lookAtEntity(livingEntity, 30.0F, 30.0F);
                    } else if (d <= (double)this.squaredRange && this.targetSeeingTicker >= 20) {
                        // 在理想攻击范围内且看到目标一段时间：进入战斗状态
                        this.actor.getNavigation().stop();
                        this.combatTicks++;

                        // 调整位置保持理想距离
                        if (d > (double)(this.squaredRange * 0.75F)) {
                            this.actor.getMoveControl().strafeTo(1.0F, 0);
                        } else if (d < (double)(this.squaredRange * 0.5F)) {
                            this.actor.getMoveControl().strafeTo(-1.0F, 0);
                        } else {
                            this.actor.getMoveControl().strafeTo(0, 0);
                        }

                        this.actor.lookAtEntity(livingEntity, 30.0F, 30.0F);
                    } else {
                        // 在攻击范围外或刚看到目标：直接走向目标
                        this.actor.getNavigation().startMovingTo(livingEntity, this.speed);
                        this.combatTicks = -1;
                        this.actor.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
                    }
                }

                // 重置战斗计时器
                if (this.combatTicks >= 20) {
                    this.combatTicks = 0;
                }

                // 射击逻辑
                if (this.actor.isUsingItem()) {
                    if (!canSeeTarget && this.targetSeeingTicker < -60) {
                        this.actor.clearActiveItem();
                    } else if (canSeeTarget) {
                        int i = this.actor.getItemUseTime();
                        if (i >= 20) {
                            this.actor.clearActiveItem();
                            this.actor.shootAt(livingEntity, BowItem.getPullProgress(i));
                            this.cooldown = this.attackInterval;
                        }
                    }
                } else if (--this.cooldown <= 0 && this.targetSeeingTicker >= -60) {
                    // 允许在撤退时射箭：只要能看到目标就可以开始拉弓
                    if (canSeeTarget) {
                        this.actor.setCurrentHand(ProjectileUtil.getHandPossiblyHolding(this.actor, Items.BOW));
                    }
                }
            }
        }
    }
}
