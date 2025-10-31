package com.equilibrium.entity.mob;

import com.equilibrium.item.Tools;
import com.google.common.annotations.VisibleForTesting;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.Unique;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

import static com.equilibrium.util.XpHashMap.getXpForLevel;

public class BaseSlimeEntity extends MobEntity implements Monster {
    private static final TrackedData<Integer> SLIME_SIZE = DataTracker.registerData(BaseSlimeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final int MIN_SIZE = 1;
    public static final int MAX_SIZE = 127;
    public static final int field_50136 = 4;
    public float targetStretch;
    public float stretch;
    public float lastStretch;
    private boolean onGroundLastTick;
    public static HashSet<Item> isCorruptibleItems = new HashSet<>();



    public BaseSlimeEntity(EntityType<? extends BaseSlimeEntity> entityType, World world) {
        super(entityType, world);
        this.reinitDimensions();
        this.moveControl = new BaseSlimeEntity.SlimeMoveControl(this);

        isCorruptibleItems.add(Items.IRON_CHESTPLATE);
        isCorruptibleItems.add(Items.IRON_HELMET);
        isCorruptibleItems.add(Items.IRON_BOOTS);
        isCorruptibleItems.add(Items.IRON_LEGGINGS);
        isCorruptibleItems.add(Tools.IRON_AXE);
        isCorruptibleItems.add(Tools.IRON_HOE);
        isCorruptibleItems.add(Tools.IRON_PICKAXE);
        isCorruptibleItems.add(Tools.IRON_DAGGER);
        isCorruptibleItems.add(Tools.IRON_SWORD);
        isCorruptibleItems.add(Tools.IRON_SHOVEL);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new BaseSlimeEntity.SwimmingGoal(this));
        this.goalSelector.add(2, new BaseSlimeEntity.FaceTowardTargetGoal(this));
        this.goalSelector.add(3, new BaseSlimeEntity.RandomLookGoal(this));
        this.goalSelector.add(5, new BaseSlimeEntity.MoveGoal(this));
        this.targetSelector
                .add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, livingEntity -> Math.abs(livingEntity.getY() - this.getY()) <= 4.0));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SLIME_SIZE, 1);
    }

    @VisibleForTesting
    public void setSize(int size, boolean heal) {
        int i = MathHelper.clamp(size, 1, 127);
        this.dataTracker.set(SLIME_SIZE, i);
        this.refreshPosition();
        this.calculateDimensions();
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue((double)(i * i));
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)i));
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue((double)i);
        if (heal) {
            this.setHealth(this.getMaxHealth());
        }
        this.experiencePoints = i;
    }

    public int getSize() {
        return this.dataTracker.get(SLIME_SIZE);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Size", this.getSize() - 1);
        nbt.putBoolean("wasOnGround", this.onGroundLastTick);
    }


    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.setSize(nbt.getInt("Size") + 1, false);
        super.readCustomDataFromNbt(nbt);
        this.onGroundLastTick = nbt.getBoolean("wasOnGround");
    }

    public boolean isSmall() {
        return this.getSize() <= 1;
    }

    protected ParticleEffect getParticles() {
        return ParticleTypes.ITEM_SLIME;
    }

    @Override
    protected boolean isDisallowedInPeaceful() {
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
    public void calculateDimensions() {
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        super.calculateDimensions();
        this.setPosition(d, e, f);
    }

    @Override
    public EntityDimensions getBaseDimensions(EntityPose pose) {
        // 基准尺寸为宽0.6F、高0.6F的立方体，再根据尺寸缩放
        return EntityDimensions.changing(0.6F, 0.6F).scaled(this.getSize());
    }




    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (SLIME_SIZE.equals(data)) {
            this.calculateDimensions();
            this.setYaw(this.headYaw);
            this.bodyYaw = this.headYaw;
            if (this.isTouchingWater() && this.random.nextInt(20) == 0) {
                this.onSwimmingStart();
            }
        }

        super.onTrackedDataSet(data);
    }

    @Override
    public EntityType<? extends BaseSlimeEntity> getType() {
        return (EntityType<? extends BaseSlimeEntity>)super.getType();
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        int i = this.getSize();
        if (!this.getWorld().isClient && i > 1 && this.isDead()) {
            Text text = this.getCustomName();
            boolean bl = this.isAiDisabled();
            float f = this.getDimensions(this.getPose()).width();
            float g = f / 2.0F;
            int j = i / 2;
            int k = 2 + this.random.nextInt(3);

            for (int l = 0; l < k; l++) {
                float h = ((float)(l % 2) - 0.5F) * g;
                float m = ((float)(l / 2) - 0.5F) * g;
                BaseSlimeEntity baseSlimeEntity = this.getType().create(this.getWorld());
                if (baseSlimeEntity != null) {
                    if (this.isPersistent()) {
                        baseSlimeEntity.setPersistent();
                    }

                    baseSlimeEntity.setCustomName(text);
                    baseSlimeEntity.setAiDisabled(bl);
                    baseSlimeEntity.setInvulnerable(this.isInvulnerable());
                    baseSlimeEntity.setSize(j, true);
                    baseSlimeEntity.refreshPositionAndAngles(this.getX() + (double)h, this.getY() + 0.5, this.getZ() + (double)m, this.random.nextFloat() * 360.0F, 0.0F);
                    this.getWorld().spawnEntity(baseSlimeEntity);
                }
            }
        }

        super.remove(reason);
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        super.pushAwayFrom(entity);
        if (entity instanceof IronGolemEntity && this.canAttack()) {
            this.damage((LivingEntity)entity);
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (this.canAttack()) {
            this.damage(player);
        }
    }


    //所有的史莱姆均具有腐蚀性
    //damage函数是攻击者和被攻击者两者之间使用,如果强调是主动攻击的一方,应该用onattack
    protected void damage(LivingEntity target) {
        if (this.isAlive() && this.isInAttackRange(target) && this.canSee(target)) {
            DamageSource damageSource = this.getDamageSources().mobAttack(this);
            if (target.damage(damageSource, this.getDamageAmount())) {
                this.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                target.getAllArmorItems().forEach(itemStack -> {
                    if(isCorruptibleItems.contains(itemStack.getItem())) {
                        itemStack.damage((int) (itemStack.getMaxDamage() * 0.05), target, this.getSlotForStack(itemStack, List.of()));
                        this.playSound(SoundEvents.BLOCK_LAVA_EXTINGUISH, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                    }
                });

                if (this.getWorld() instanceof ServerWorld serverWorld) {
                    EnchantmentHelper.onTargetDamaged(serverWorld, target, damageSource);
                }
            }
        }
    }

    @Override
    protected Vec3d getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return new Vec3d(0.0, (double)dimensions.height() - 0.015625 * (double)this.getSize() * (double)scaleFactor, 0.0);
    }

    protected boolean canAttack() {
        return !this.isSmall() && this.canMoveVoluntarily();
    }

    protected float getDamageAmount() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.isSmall() ? SoundEvents.ENTITY_SLIME_HURT_SMALL : SoundEvents.ENTITY_SLIME_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isSmall() ? SoundEvents.ENTITY_SLIME_DEATH_SMALL : SoundEvents.ENTITY_SLIME_DEATH;
    }

    protected SoundEvent getSquishSound() {
        return this.isSmall() ? SoundEvents.ENTITY_SLIME_SQUISH_SMALL : SoundEvents.ENTITY_SLIME_SQUISH;
    }

    public static boolean canSpawn(EntityType<SlimeEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        if (SpawnReason.isAnySpawner(spawnReason)) {
            return canMobSpawn(type, world, spawnReason, pos, random);
        } else {
            if (world.getDifficulty() != Difficulty.PEACEFUL) {
                if (spawnReason == SpawnReason.SPAWNER) {
                    return canMobSpawn(type, world, spawnReason, pos, random);
                }

                if (world.getBiome(pos).isIn(BiomeTags.ALLOWS_SURFACE_SLIME_SPAWNS)
                        && pos.getY() > 50
                        && pos.getY() < 70
                        && random.nextFloat() < 0.5F
                        && random.nextFloat() < world.getMoonSize()
                        && world.getLightLevel(pos) <= random.nextInt(8)) {
                    return canMobSpawn(type, world, spawnReason, pos, random);
                }

                if (!(world instanceof StructureWorldAccess)) {
                    return false;
                }

                ChunkPos chunkPos = new ChunkPos(pos);
                boolean bl = ChunkRandom.getSlimeRandom(chunkPos.x, chunkPos.z, ((StructureWorldAccess)world).getSeed(), 987234911L).nextInt(10) == 0;
                if (random.nextInt(10) == 0 && bl && pos.getY() < 40) {
                    return canMobSpawn(type, world, spawnReason, pos, random);
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
    public int getMaxLookPitchChange() {
        return 0;
    }

    protected boolean makesJumpSound() {
        return this.getSize() > 0;
    }

    @Override
    public void jump() {
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x, (double)this.getJumpVelocity(), vec3d.z);
        this.velocityDirty = true;
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
        return this.isSmall() ? SoundEvents.ENTITY_SLIME_JUMP_SMALL : SoundEvents.ENTITY_SLIME_JUMP;
    }



    static class FaceTowardTargetGoal extends Goal {
        private final BaseSlimeEntity slime;
        private int ticksLeft;

        public FaceTowardTargetGoal(BaseSlimeEntity slime) {
            this.slime = slime;
            this.setControls(EnumSet.of(Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = this.slime.getTarget();
            if (livingEntity == null) {
                return false;
            } else {
                return !this.slime.canTarget(livingEntity) ? false : this.slime.getMoveControl() instanceof BaseSlimeEntity.SlimeMoveControl;
            }
        }

        @Override
        public void start() {
            this.ticksLeft = toGoalTicks(300);
            super.start();
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity livingEntity = this.slime.getTarget();
            if (livingEntity == null) {
                return false;
            } else {
                return !this.slime.canTarget(livingEntity) ? false : --this.ticksLeft > 0;
            }
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = this.slime.getTarget();
            if (livingEntity != null) {
                this.slime.lookAtEntity(livingEntity, 10.0F, 10.0F);
            }

            if (this.slime.getMoveControl() instanceof BaseSlimeEntity.SlimeMoveControl slimeMoveControl) {
                slimeMoveControl.look(this.slime.getYaw(), this.slime.canAttack());
            }
        }
    }

    static class MoveGoal extends Goal {
        private final BaseSlimeEntity slime;

        public MoveGoal(BaseSlimeEntity slime) {
            this.slime = slime;
            this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return !this.slime.hasVehicle();
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
            this.setControls(EnumSet.of(Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return this.slime.getTarget() == null
                    && (this.slime.isOnGround() || this.slime.isTouchingWater() || this.slime.isInLava() || this.slime.hasStatusEffect(StatusEffects.LEVITATION))
                    && this.slime.getMoveControl() instanceof BaseSlimeEntity.SlimeMoveControl;
        }

        @Override
        public void tick() {
            if (--this.timer <= 0) {
                this.timer = this.getTickCount(40 + this.slime.getRandom().nextInt(60));
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
            this.targetYaw = 180.0F * slime.getYaw() / (float) Math.PI;
        }

        public void look(float targetYaw, boolean jumpOften) {
            this.targetYaw = targetYaw;
            this.jumpOften = jumpOften;
        }

        public void move(double speed) {
            this.speed = speed;
            this.state = MoveControl.State.MOVE_TO;
        }

        @Override
        public void tick() {
            this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), this.targetYaw, 90.0F));
            this.entity.headYaw = this.entity.getYaw();
            this.entity.bodyYaw = this.entity.getYaw();
            if (this.state != MoveControl.State.MOVE_TO) {
                this.entity.setForwardSpeed(0.0F);
            } else {
                this.state = MoveControl.State.WAIT;
                if (this.entity.isOnGround()) {
                    this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
                    if (this.ticksUntilJump-- <= 0) {
                        this.ticksUntilJump = this.slime.getTicksUntilNextJump();
                        if (this.jumpOften) {
                            this.ticksUntilJump /= 3;
                        }

                        this.slime.getJumpControl().setActive();
                        if (this.slime.makesJumpSound()) {
                            this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getJumpSoundPitch());
                        }
                    } else {
                        this.slime.sidewaysSpeed = 0.0F;
                        this.slime.forwardSpeed = 0.0F;
                        this.entity.setMovementSpeed(0.0F);
                    }
                } else {
                    this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
                }
            }
        }
    }

    static class SwimmingGoal extends Goal {
        private final BaseSlimeEntity slime;

        public SwimmingGoal(BaseSlimeEntity slime) {
            this.slime = slime;
            this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
            slime.getNavigation().setCanSwim(true);
        }

        @Override
        public boolean canStart() {
            return (this.slime.isTouchingWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof BaseSlimeEntity.SlimeMoveControl;
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.slime.getRandom().nextFloat() < 0.8F) {
                this.slime.getJumpControl().setActive();
            }

            if (this.slime.getMoveControl() instanceof BaseSlimeEntity.SlimeMoveControl slimeMoveControl) {
                slimeMoveControl.move(1.2);
            }
        }
    }
}
