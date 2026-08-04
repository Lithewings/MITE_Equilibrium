package com.equilibrium.entity.mob;

import com.equilibrium.entity.goal.MeleeAttackGoalApplyAttackRange;
import com.equilibrium.tags.ModItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.EnumSet;

public abstract class ModAbstractSkeletonEntity extends Monster implements RangedAttackMob {
    private static final int HARD_ATTACK_INTERVAL = 20;
    private static final int REGULAR_ATTACK_INTERVAL = 40;
    private final BowAttackGoal<ModAbstractSkeletonEntity> bowAttackGoal = new BowAttackGoal<>(this, 1.0, 20, 15.0F);
    private final MeleeAttackGoalApplyAttackRange meleeAttackGoal = new MeleeAttackGoalApplyAttackRange(this, 1.2, true,1.5f) {
        @Override
        public void stop() {
            super.stop();
            ModAbstractSkeletonEntity.this.setAggressive(false);
        }

        @Override
        public void start() {
            super.start();
            ModAbstractSkeletonEntity.this.setAggressive(true);
        }

        @Override
        public float getAttackRange() {
            return this.mob.getMainHandItem().is(ModItemTags.SWORDS)?3f:1.5f;
        }

    };

    protected ModAbstractSkeletonEntity(EntityType<? extends ModAbstractSkeletonEntity> entityType, Level world) {
        super(entityType, world);
        this.updateAttackType();
    }



    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0));
        this.goalSelector.addGoal(3, new AvoidEntityGoal(this, Wolf.class, 6.0F, 1.0, 1.2));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    public static AttributeSupplier.Builder createAbstractSkeletonAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    abstract SoundEvent getStepSound();

    @Override
    public void aiStep() {
        boolean bl = this.isSunBurnTick();
        if (bl) {
            ItemStack itemStack = this.getItemBySlot(EquipmentSlot.HEAD);
            if (!itemStack.isEmpty()) {
                if (itemStack.isDamageableItem()) {
                    Item item = itemStack.getItem();
                    itemStack.setDamageValue(itemStack.getDamageValue() + this.random.nextInt(2));
                    if (itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
                        this.onEquippedItemBroken(item, EquipmentSlot.HEAD);
                        this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                    }
                }

                bl = false;
            }

            if (bl) {
                this.igniteForSeconds(8.0F);
            }
        }

        super.aiStep();
    }

    @Override
    public void rideTick() {
        super.rideTick();
        if (this.getControlledVehicle() instanceof PathfinderMob pathAwareEntity) {
            this.yBodyRot = pathAwareEntity.yBodyRot;
        }
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance localDifficulty) {
        super.populateDefaultEquipmentSlots(random, localDifficulty);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData) {
        entityData = super.finalizeSpawn(world, difficulty, spawnReason, entityData);
        RandomSource random = world.getRandom();
        this.populateDefaultEquipmentSlots(random, difficulty);
        this.populateDefaultEquipmentEnchantments(world, random, difficulty);
        this.updateAttackType();
        this.setCanPickUpLoot(random.nextFloat() < 0.55F * difficulty.getSpecialMultiplier());
        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            LocalDate localDate = LocalDate.now();
            int i = localDate.get(ChronoField.DAY_OF_MONTH);
            int j = localDate.get(ChronoField.MONTH_OF_YEAR);
            if (j == 10 && i == 31 && random.nextFloat() < 0.25F) {
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.armorDropChances[EquipmentSlot.HEAD.getIndex()] = 0.0F;
            }
        }

        return entityData;
    }

    public void updateAttackType() {
        if (this.level() != null && !this.level().isClientSide) {
            this.goalSelector.removeGoal(this.meleeAttackGoal);
            this.goalSelector.removeGoal(this.bowAttackGoal);
            ItemStack itemStack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW));
            if (itemStack.is(Items.BOW)) {
                int i = this.getHardAttackInterval();
                if (this.level().getDifficulty() != Difficulty.HARD) {
                    i = this.getRegularAttackInterval();
                }

                this.bowAttackGoal.setAttackInterval(i);
                this.goalSelector.addGoal(4, this.bowAttackGoal);
            } else {
                this.goalSelector.addGoal(4, this.meleeAttackGoal);
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
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        ItemStack itemStack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW));
        ItemStack itemStack2 = this.getProjectile(itemStack);
        AbstractArrow persistentProjectileEntity = this.createArrowProjectile(itemStack2, pullProgress, itemStack);
        double d = target.getX() - this.getX();
        double e = target.getY(0.3333333333333333) - persistentProjectileEntity.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        persistentProjectileEntity.shoot(d, e + g * 0.2F, f, 1.6F, (float)(14 - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(persistentProjectileEntity);
    }

    protected AbstractArrow createArrowProjectile(ItemStack arrow, float damageModifier, @Nullable ItemStack shotFrom) {
        return ProjectileUtil.getMobArrow(this, arrow, damageModifier, shotFrom);
    }

    @Override
    public boolean canFireProjectileWeapon(ProjectileWeaponItem weapon) {
        return weapon == Items.BOW;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.updateAttackType();
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        super.setItemSlot(slot, stack);
        if (!this.level().isClientSide) {
            this.updateAttackType();
        }
    }

    public boolean isShaking() {
        return this.isFullyFrozen();
    }



    class BowAttackGoal<T extends Monster & RangedAttackMob> extends Goal {
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
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public void setAttackInterval(int attackInterval) {
            this.attackInterval = attackInterval;
        }

        @Override
        public boolean canUse() {
            return this.actor.getTarget() == null ? false : this.isHoldingBow();
        }

        protected boolean isHoldingBow() {
            return this.actor.isHolding(Items.BOW);
        }

        @Override
        public boolean canContinueToUse() {
            return (this.canUse() || !this.actor.getNavigation().isDone()) && this.isHoldingBow();
        }

        @Override
        public void start() {
            super.start();
            this.actor.setAggressive(true);
        }

        @Override
        public void stop() {
            super.stop();
            this.actor.setAggressive(false);
            this.targetSeeingTicker = 0;
            this.cooldown = -1;
            this.actor.stopUsingItem();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = this.actor.getTarget();
            if (livingEntity != null) {
                double d = this.actor.distanceToSqr(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                boolean canSeeTarget = this.actor.getSensing().hasLineOfSight(livingEntity);
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
                    this.actor.getNavigation().moveTo(livingEntity, this.speed);
                    this.combatTicks = -1;
                } else {
                    // 能看到目标，根据距离决定行为
                    if (d < (double)(this.squaredRange * 0.25F)) {
                        // 距离太近：一边向后撤退一边射箭
                        this.actor.getNavigation().stop(); // 停止导航移动

                        // 向后移动拉开距离
                        this.actor.getMoveControl().strafe(-1F, 0);

                        // 保持战斗状态，允许射箭
                        if (this.targetSeeingTicker >= 10) { // 降低阈值，更快进入射击状态
                            this.combatTicks = Math.max(this.combatTicks, 0);
                        }

                        // 始终看向目标
                        this.actor.lookAt(livingEntity, 30.0F, 30.0F);
                    } else if (d <= (double)this.squaredRange && this.targetSeeingTicker >= 20) {
                        // 在理想攻击范围内且看到目标一段时间：进入战斗状态
                        this.actor.getNavigation().stop();
                        this.combatTicks++;

                        // 调整位置保持理想距离
                        if (d > (double)(this.squaredRange * 0.75F)) {
                            this.actor.getMoveControl().strafe(1.0F, 0);
                        } else if (d < (double)(this.squaredRange * 0.5F)) {
                            this.actor.getMoveControl().strafe(-1.0F, 0);
                        } else {
                            this.actor.getMoveControl().strafe(0, 0);
                        }

                        this.actor.lookAt(livingEntity, 30.0F, 30.0F);
                    } else {
                        // 在攻击范围外或刚看到目标：直接走向目标
                        this.actor.getNavigation().moveTo(livingEntity, this.speed);
                        this.combatTicks = -1;
                        this.actor.getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);
                    }
                }

                // 重置战斗计时器
                if (this.combatTicks >= 20) {
                    this.combatTicks = 0;
                }

                // 射击逻辑
                if (this.actor.isUsingItem()) {
                    if (!canSeeTarget && this.targetSeeingTicker < -60) {
                        this.actor.stopUsingItem();
                    } else if (canSeeTarget) {
                        int i = this.actor.getTicksUsingItem();
                        if (i >= 20) {
                            this.actor.stopUsingItem();
                            this.actor.performRangedAttack(livingEntity, BowItem.getPowerForTime(i));
                            this.cooldown = this.attackInterval;
                        }
                    }
                } else if (--this.cooldown <= 0 && this.targetSeeingTicker >= -60) {
                    // 允许在撤退时射箭：只要能看到目标就可以开始拉弓
                    if (canSeeTarget) {
                        this.actor.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.actor, Items.BOW));
                    }
                }
            }
        }
    }
}
