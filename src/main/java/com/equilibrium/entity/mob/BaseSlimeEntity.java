package com.equilibrium.entity.mob;

import com.equilibrium.item.Tools;
import com.google.common.annotations.VisibleForTesting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Unique;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

import static com.equilibrium.util.XpHashMap.getXpForLevel;

public class BaseSlimeEntity extends Mob implements Enemy {
    private static final EntityDataAccessor<Integer> SLIME_SIZE = SynchedEntityData.defineId(BaseSlimeEntity.class, EntityDataSerializers.INT);
    public static final int MIN_SIZE = 1;
    public static final int MAX_SIZE = 127;
    public static final int field_50136 = 4;
    public float targetStretch;
    public float stretch;
    public float lastStretch;
    private boolean onGroundLastTick;
    public static HashSet<Item> isCorruptibleItems = new HashSet<>();



    public BaseSlimeEntity(EntityType<? extends BaseSlimeEntity> entityType, Level world) {
        super(entityType, world);
        this.fixupDimensions();
        this.moveControl = new BaseSlimeEntity.SlimeMoveControl(this);

        isCorruptibleItems.add(Items.IRON_CHESTPLATE);
        isCorruptibleItems.add(Items.IRON_HELMET);
        isCorruptibleItems.add(Items.IRON_BOOTS);
        isCorruptibleItems.add(Items.IRON_LEGGINGS);
        isCorruptibleItems.add(Tools.IRON_AXE.get());
        isCorruptibleItems.add(Tools.IRON_HOE.get());
        isCorruptibleItems.add(Tools.IRON_PICKAXE.get());
        isCorruptibleItems.add(Tools.IRON_DAGGER.get());
        isCorruptibleItems.add(Tools.IRON_SWORD.get());
        isCorruptibleItems.add(Tools.IRON_SHOVEL.get());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new BaseSlimeEntity.SwimmingGoal(this));
        this.goalSelector.addGoal(2, new BaseSlimeEntity.FaceTowardTargetGoal(this));
        this.goalSelector.addGoal(3, new BaseSlimeEntity.RandomLookGoal(this));
        this.goalSelector.addGoal(5, new BaseSlimeEntity.MoveGoal(this));
        this.targetSelector
                .addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, livingEntity -> Math.abs(livingEntity.getY() - this.getY()) <= 4.0));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SLIME_SIZE, 1);
    }

    @VisibleForTesting
    public void setSize(int size, boolean heal) {
        int i = Mth.clamp(size, 1, 127);
        this.entityData.set(SLIME_SIZE, i);
        this.reapplyPosition();
        this.refreshDimensions();
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)(i * i));
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)i));
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double)i);
        if (heal) {
            this.setHealth(this.getMaxHealth());
        }
        this.xpReward = i;
    }

    public int getSize() {
        return this.entityData.get(SLIME_SIZE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("Size", this.getSize() - 1);
        nbt.putBoolean("wasOnGround", this.onGroundLastTick);
    }


    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        this.setSize(nbt.getInt("Size") + 1, false);
        super.readAdditionalSaveData(nbt);
        this.onGroundLastTick = nbt.getBoolean("wasOnGround");
    }

    public boolean isSmall() {
        return this.getSize() <= 1;
    }

    protected ParticleOptions getParticles() {
        return ParticleTypes.ITEM_SLIME;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
    }

    protected void updateStretch() {
        this.targetStretch *= 0.6F;
    }

    protected int getTicksUntilNextJump() {
        return this.random.nextInt(20) + 10;
    }

    @Override
    public void refreshDimensions() {
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        super.refreshDimensions();
        this.setPos(d, e, f);
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        // 基准尺寸为宽0.6F、高0.6F的立方体，再根据尺寸缩放
        return EntityDimensions.scalable(0.6F, 0.6F).scale(this.getSize());
    }




    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        if (SLIME_SIZE.equals(data)) {
            this.refreshDimensions();
            this.setYRot(this.yHeadRot);
            this.yBodyRot = this.yHeadRot;
            if (this.isInWater() && this.random.nextInt(20) == 0) {
                this.doWaterSplashEffect();
            }
        }

        super.onSyncedDataUpdated(data);
    }

    @Override
    public EntityType<? extends BaseSlimeEntity> getType() {
        return (EntityType<? extends BaseSlimeEntity>)super.getType();
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        int i = this.getSize();
        if (!this.level().isClientSide && i > 1 && this.isDeadOrDying()) {
            Component text = this.getCustomName();
            boolean bl = this.isNoAi();
            float f = this.getDimensions(this.getPose()).width();
            float g = f / 2.0F;
            int j = i / 2;
            int k = 2 + this.random.nextInt(3);

            for (int l = 0; l < k; l++) {
                float h = ((float)(l % 2) - 0.5F) * g;
                float m = ((float)(l / 2) - 0.5F) * g;
                BaseSlimeEntity baseSlimeEntity = this.getType().create(this.level());
                if (baseSlimeEntity != null) {
                    if (this.isPersistenceRequired()) {
                        baseSlimeEntity.setPersistenceRequired();
                    }

                    baseSlimeEntity.setCustomName(text);
                    baseSlimeEntity.setNoAi(bl);
                    baseSlimeEntity.setInvulnerable(this.isInvulnerable());
                    baseSlimeEntity.setSize(j, true);
                    baseSlimeEntity.moveTo(this.getX() + (double)h, this.getY() + 0.5, this.getZ() + (double)m, this.random.nextFloat() * 360.0F, 0.0F);
                    this.level().addFreshEntity(baseSlimeEntity);
                }
            }
        }

        super.remove(reason);
    }

    @Override
    public void push(Entity entity) {
        super.push(entity);
        if (entity instanceof IronGolem && this.canAttack()) {
            this.damage((LivingEntity)entity);
        }
    }

    @Override
    public void playerTouch(Player player) {
        if (this.canAttack()) {
            this.damage(player);
        }
    }


    //所有的史莱姆均具有腐蚀性
    //damage函数是攻击者和被攻击者两者之间使用,如果强调是主动攻击的一方,应该用onattack
    protected void damage(LivingEntity target) {
        if (this.isAlive() && this.isWithinMeleeAttackRange(target) && this.hasLineOfSight(target)) {
            DamageSource damageSource = this.damageSources().mobAttack(this);
            if (target.hurt(damageSource, this.getDamageAmount())) {
                this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                target.getArmorAndBodyArmorSlots().forEach(itemStack -> {
                    if(isCorruptibleItems.contains(itemStack.getItem())) {
                        itemStack.hurtAndBreak((int) (itemStack.getMaxDamage() * 0.05), target, this.resolveSlot(itemStack, List.of()));
                        this.playSound(SoundEvents.LAVA_EXTINGUISH, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                    }
                });

                if (this.level() instanceof ServerLevel serverWorld) {
                    EnchantmentHelper.doPostAttackEffects(serverWorld, target, damageSource);
                }
            }
        }
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return new Vec3(0.0, (double)dimensions.height() - 0.015625 * (double)this.getSize() * (double)scaleFactor, 0.0);
    }

    protected boolean canAttack() {
        return !this.isSmall() && this.isEffectiveAi();
    }

    protected float getDamageAmount() {
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.isSmall() ? SoundEvents.SLIME_HURT_SMALL : SoundEvents.SLIME_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isSmall() ? SoundEvents.SLIME_DEATH_SMALL : SoundEvents.SLIME_DEATH;
    }

    protected SoundEvent getSquishSound() {
        return this.isSmall() ? SoundEvents.SLIME_SQUISH_SMALL : SoundEvents.SLIME_SQUISH;
    }

    public static boolean canSpawn(EntityType<Slime> type, LevelAccessor world, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        if (MobSpawnType.isSpawner(spawnReason)) {
            return checkMobSpawnRules(type, world, spawnReason, pos, random);
        } else {
            if (world.getDifficulty() != Difficulty.PEACEFUL) {
                if (spawnReason == MobSpawnType.SPAWNER) {
                    return checkMobSpawnRules(type, world, spawnReason, pos, random);
                }

                if (world.getBiome(pos).is(BiomeTags.ALLOWS_SURFACE_SLIME_SPAWNS)
                        && pos.getY() > 50
                        && pos.getY() < 70
                        && random.nextFloat() < 0.5F
                        && random.nextFloat() < world.getMoonBrightness()
                        && world.getMaxLocalRawBrightness(pos) <= random.nextInt(8)) {
                    return checkMobSpawnRules(type, world, spawnReason, pos, random);
                }

                if (!(world instanceof WorldGenLevel)) {
                    return false;
                }

                ChunkPos chunkPos = new ChunkPos(pos);
                boolean bl = WorldgenRandom.seedSlimeChunk(chunkPos.x, chunkPos.z, ((WorldGenLevel)world).getSeed(), 987234911L).nextInt(10) == 0;
                if (random.nextInt(10) == 0 && bl && pos.getY() < 40) {
                    return checkMobSpawnRules(type, world, spawnReason, pos, random);
                }
            }

            return false;
        }
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F * (float)this.getSize();
    }

    @Override
    public int getMaxHeadXRot() {
        return 0;
    }

    protected boolean makesJumpSound() {
        return this.getSize() > 0;
    }

    @Override
    public void jumpFromGround() {
        Vec3 vec3d = this.getDeltaMovement();
        this.setDeltaMovement(vec3d.x, (double)this.getJumpPower(), vec3d.z);
        this.hasImpulse = true;
    }


//    @Override
//    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
//        Random random = world.getRandom();
//        int i = random.nextInt(3);
//        if (i < 2 && random.nextFloat() < 0.5F * difficulty.getClampedLocalDifficulty()) {
//            i++;
//        }
//
//        int j = 1 << i;
//        this.setSize(j, true);
//        return super.initialize(world, difficulty, spawnReason, entityData);
//    }

    float getJumpSoundPitch() {
        float f = this.isSmall() ? 1.4F : 0.8F;
        return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * f;
    }

    protected SoundEvent getJumpSound() {
        return this.isSmall() ? SoundEvents.SLIME_JUMP_SMALL : SoundEvents.SLIME_JUMP;
    }



    static class FaceTowardTargetGoal extends Goal {
        private final BaseSlimeEntity slime;
        private int ticksLeft;

        public FaceTowardTargetGoal(BaseSlimeEntity slime) {
            this.slime = slime;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingEntity = this.slime.getTarget();
            if (livingEntity == null) {
                return false;
            } else {
                return !this.slime.canAttack(livingEntity) ? false : this.slime.getMoveControl() instanceof BaseSlimeEntity.SlimeMoveControl;
            }
        }

        @Override
        public void start() {
            this.ticksLeft = reducedTickDelay(300);
            super.start();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity livingEntity = this.slime.getTarget();
            if (livingEntity == null) {
                return false;
            } else {
                return !this.slime.canAttack(livingEntity) ? false : --this.ticksLeft > 0;
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = this.slime.getTarget();
            if (livingEntity != null) {
                this.slime.lookAt(livingEntity, 10.0F, 10.0F);
            }

            if (this.slime.getMoveControl() instanceof BaseSlimeEntity.SlimeMoveControl slimeMoveControl) {
                slimeMoveControl.look(this.slime.getYRot(), this.slime.canAttack());
            }
        }
    }

    static class MoveGoal extends Goal {
        private final BaseSlimeEntity slime;

        public MoveGoal(BaseSlimeEntity slime) {
            this.slime = slime;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !this.slime.isPassenger();
        }

        @Override
        public void tick() {
            if (this.slime.getMoveControl() instanceof BaseSlimeEntity.SlimeMoveControl slimeMoveControl) {
                slimeMoveControl.move(1.0);
            }
        }
    }

    static class RandomLookGoal extends Goal {
        private final BaseSlimeEntity slime;
        private float targetYaw;
        private int timer;

        public RandomLookGoal(BaseSlimeEntity slime) {
            this.slime = slime;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.slime.getTarget() == null
                    && (this.slime.onGround() || this.slime.isInWater() || this.slime.isInLava() || this.slime.hasEffect(MobEffects.LEVITATION))
                    && this.slime.getMoveControl() instanceof BaseSlimeEntity.SlimeMoveControl;
        }

        @Override
        public void tick() {
            if (--this.timer <= 0) {
                this.timer = this.adjustedTickDelay(40 + this.slime.getRandom().nextInt(60));
                this.targetYaw = (float)this.slime.getRandom().nextInt(360);
            }

            if (this.slime.getMoveControl() instanceof BaseSlimeEntity.SlimeMoveControl slimeMoveControl) {
                slimeMoveControl.look(this.targetYaw, false);
            }
        }
    }

    static class SlimeMoveControl extends MoveControl {
        private float targetYaw;
        private int ticksUntilJump;
        private final BaseSlimeEntity slime;
        private boolean jumpOften;

        public SlimeMoveControl(BaseSlimeEntity slime) {
            super(slime);
            this.slime = slime;
            this.targetYaw = 180.0F * slime.getYRot() / (float) Math.PI;
        }

        public void look(float targetYaw, boolean jumpOften) {
            this.targetYaw = targetYaw;
            this.jumpOften = jumpOften;
        }

        public void move(double speed) {
            this.speedModifier = speed;
            this.operation = MoveControl.Operation.MOVE_TO;
        }

        @Override
        public void tick() {
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.targetYaw, 90.0F));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
            if (this.operation != MoveControl.Operation.MOVE_TO) {
                this.mob.setZza(0.0F);
            } else {
                this.operation = MoveControl.Operation.WAIT;
                if (this.mob.onGround()) {
                    this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                    if (this.ticksUntilJump-- <= 0) {
                        this.ticksUntilJump = this.slime.getTicksUntilNextJump();
                        if (this.jumpOften) {
                            this.ticksUntilJump /= 3;
                        }

                        this.slime.getJumpControl().jump();
                        if (this.slime.makesJumpSound()) {
                            this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getJumpSoundPitch());
                        }
                    } else {
                        this.slime.xxa = 0.0F;
                        this.slime.zza = 0.0F;
                        this.mob.setSpeed(0.0F);
                    }
                } else {
                    this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }
            }
        }
    }

    static class SwimmingGoal extends Goal {
        private final BaseSlimeEntity slime;

        public SwimmingGoal(BaseSlimeEntity slime) {
            this.slime = slime;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
            slime.getNavigation().setCanFloat(true);
        }

        @Override
        public boolean canUse() {
            return (this.slime.isInWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof BaseSlimeEntity.SlimeMoveControl;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.slime.getRandom().nextFloat() < 0.8F) {
                this.slime.getJumpControl().jump();
            }

            if (this.slime.getMoveControl() instanceof BaseSlimeEntity.SlimeMoveControl slimeMoveControl) {
                slimeMoveControl.move(1.2);
            }
        }
    }
}
