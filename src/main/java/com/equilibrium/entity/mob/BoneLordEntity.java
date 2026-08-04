package com.equilibrium.entity.mob;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.item.Armors;
import com.equilibrium.item.Tools;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.function.Predicate;

import static com.equilibrium.entity.ModEntities.LONG_DEAD;
import static com.equilibrium.util.XpHashMap.getXpForLevel;
import static net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN;

public class BoneLordEntity extends LongDeadEntity {
    private boolean shouldSummonArmy = true;

    @Unique
    private final SimpleContainer meleeInventory = new SimpleContainer(1);
    @Unique
    private final SimpleContainer rangeAttackInventory = new SimpleContainer(1);


    public BoneLordEntity(EntityType<? extends ModAbstractSkeletonEntity> entityType, Level world) {
        super(entityType, world);
        int hammerOrSword = this.getRandom().nextInt(2);
        meleeInventory.addItem(hammerOrSword == 0 ? new ItemStack(Tools.MITHRIL_SWORD.get()) : new ItemStack(Tools.MITHRIL_HAMMER.get()));
        rangeAttackInventory.addItem(new ItemStack(Items.BOW));

    }

    @Override
    protected int getBaseExperienceReward() {
        //5000xp
        return getXpForLevel(5) * 10;
    }



    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance localDifficulty) {
        super.populateDefaultEquipmentSlots(random, localDifficulty);


        this.setItemSlot(EquipmentSlot.MAINHAND, rangeAttackInventory.getItem(0));
        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Armors.MITHRIL_CHEST_PLATE));
        this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Armors.MITHRIL_BOOTS));
        this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Armors.MITHRIL_LEGGINGS));
        this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Armors.MITHRIL_HELMET));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Wolf.class, 6.0F, 1.0, 1.2));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));


        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new BoneLordTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));


    }


    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("shouldSummonArmy", this.shouldSummonArmy);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("shouldSummonArmy")) {
            this.shouldSummonArmy= nbt.getBoolean("shouldSummonArmy");
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        if (!this.isRemoved() && !this.dead) {
            Entity entity = damageSource.getEntity();
            LivingEntity livingEntity = this.getKillCredit();
            if (this.deathScore >= 0 && livingEntity != null) {
                livingEntity.awardKillScore(this, this.deathScore, damageSource);
            }

            if (this.isSleeping()) {
                this.stopSleeping();
            }

            if (!this.level().isClientSide && this.hasCustomName()) {
                OnServerInitialize.LOGGER.info("Named entity {} died: {}", this, this.getCombatTracker().getDeathMessage().getString());
            }

            this.dead = true;
            this.getCombatTracker().recheckStatus();
            if (this.level() instanceof ServerLevel serverWorld) {
                if (entity == null || entity.killedEntity(serverWorld, this)) {
                    this.gameEvent(GameEvent.ENTITY_DIE);
                    this.dropAllDeathLoot(serverWorld, damageSource);
                    this.createWitherRose(livingEntity);
                }
                this.level().broadcastEntityEvent(this, EntityEvent.DEATH);
            }
            this.setPose(Pose.DYING);
        }
    }

    protected void dropAllDeathLoot(ServerLevel world, DamageSource damageSource) {
        this.dropEquipment();
        this.dropExperience(damageSource.getEntity());
    }

    @Override
    public void setLastHurtMob(Entity target) {
        super.setLastHurtMob(target);
        if(target instanceof Player player && this.getMainHandItem().is(Tools.MITHRIL_HAMMER.get())){
            player.addEffect(new MobEffectInstance(MOVEMENT_SLOWDOWN,100,2));
        }
    }


    class BoneLordTargetGoal<T extends LivingEntity> extends TargetGoal {
        protected final Class<T> targetClass;
        /**
         * The reciprocal of chance to actually search for a target on every tick
         * when this goal is not started. This is also the average number of ticks
         * between each search (as in a poisson distribution).
         */
        protected final int reciprocalChance;
        @Nullable
        protected LivingEntity targetEntity;
        protected TargetingConditions targetPredicate;


        public BoneLordTargetGoal(Mob mob, Class<T> targetClass, boolean checkVisibility) {
            this(mob, targetClass, 10, checkVisibility, false, null);
        }

        public BoneLordTargetGoal(
                Mob mob,
                Class<T> targetClass,
                int reciprocalChance,
                boolean checkVisibility,
                boolean checkCanNavigate,
                @Nullable Predicate<LivingEntity> targetPredicate
        ) {
            super(mob, checkVisibility, checkCanNavigate);
            this.targetClass = targetClass;
            this.reciprocalChance = reducedTickDelay(reciprocalChance);
            this.setFlags(EnumSet.of(Goal.Flag.TARGET));
            this.targetPredicate = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(targetPredicate);
        }

        @Override
        public boolean canUse() {
            this.findClosestTarget();
            return this.targetEntity != null;

        }


        private ArrayList<BlockPos> getAvailablePositions(BlockPos mobPos) {
            ArrayList<BlockPos> optionalPos = new ArrayList<>();


            Level world = this.mob.getCommandSenderWorld();

            optionalPos.add(mobPos.east());
            optionalPos.add(mobPos.south());
            optionalPos.add(mobPos.west());
            optionalPos.add(mobPos.north());


            ArrayList<BlockPos> availablePos = new ArrayList<>();


            for (BlockPos pos : optionalPos) {


                for (int y = -2; y <= 2; y++) {
                    BlockPos checkPos = pos.offset(0, y, 0);
                    boolean canStand = world.getBlockState(checkPos.below()).isCollisionShapeFullBlock(world, checkPos);
                    boolean enoughSpace = world.getBlockState(checkPos).isAir() && world.getBlockState(checkPos.above()).isAir();
                    if (canStand && enoughSpace) {
                        availablePos.add(checkPos);
                        break;
                    }
                }


            }
            return availablePos;
        }

        private void summonArmy(BlockPos mobPos) {
            for (BlockPos pos : getAvailablePositions(mobPos)) {
                if (this.mob.level() instanceof ServerLevel serverWorld) {
                    LongDeadEntity army = LONG_DEAD.spawn(serverWorld, pos, MobSpawnType.MOB_SUMMONED);
                    if (army != null) {
                        army.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 1, false, false));
                        army.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 1,false,false));
                        army.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 1, false,true));
                    }
                }
            }
        }


        protected AABB getSearchBox(double distance) {
            return this.mob.getBoundingBox().inflate(distance, 4.0, distance);
        }


        protected void findClosestTarget() {
            if (this.targetClass != Player.class && this.targetClass != ServerPlayer.class) {
                this.targetEntity = this.mob
                        .level()
                        .getNearestEntity(
                                this.mob.level().getEntitiesOfClass(this.targetClass, this.getSearchBox(this.getFollowDistance()), livingEntity -> true),
                                this.targetPredicate,
                                this.mob,
                                this.mob.getX(),
                                this.mob.getEyeY(),
                                this.mob.getZ()
                        );
            } else {
                this.targetEntity = this.mob.level().getNearestPlayer(this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
            }
        }

        @Override
        public void start() {
            this.mob.setTarget(this.targetEntity);
            BoneLordEntity mob = (BoneLordEntity) this.mob;
            if (mob.shouldSummonArmy) {
                summonArmy(this.mob.blockPosition());
                shouldSummonArmy = false;
            }

            super.start();
        }

        @Override
        public void tick() {


            // 检查当前主手物品
            ItemStack currentStack = this.mob.getItemInHand(InteractionHand.MAIN_HAND);
            float distance = this.mob.distanceTo(targetEntity);

            if (this.targetEntity != null) {
                if (distance >= 8) {
                    // 应该使用远程武器
                    ItemStack rangeWeapon = rangeAttackInventory.getItem(0);
                    if (!currentStack.is(rangeWeapon.getItem())) {
                        this.mob.setItemInHand(InteractionHand.MAIN_HAND, rangeWeapon);
                    }
                } else {
                    // 应该使用近战武器
                    ItemStack meleeWeapon = meleeInventory.getItem(0);
                    if (!currentStack.is(meleeWeapon.getItem())) {
                        this.mob.setItemInHand(InteractionHand.MAIN_HAND, meleeWeapon);
                    }
                }
            }
        }
    }


}
