package com.equilibrium.entity.mob;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.item.Armors;
import com.equilibrium.item.Tools;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.function.Predicate;

import static com.equilibrium.entity.ModEntities.LONG_DEAD;
import static com.equilibrium.util.XpHashMap.getXpForLevel;
import static net.minecraft.entity.effect.StatusEffects.SLOWNESS;

public class BoneLordEntity extends LongDeadEntity {
    private boolean shouldSummonArmy = true;

    @Unique
    private final SimpleInventory meleeInventory = new SimpleInventory(1);
    @Unique
    private final SimpleInventory rangeAttackInventory = new SimpleInventory(1);


    public BoneLordEntity(EntityType<? extends ModAbstractSkeletonEntity> entityType, World world) {
        super(entityType, world);
        int hammerOrSword = this.getRandom().nextInt(2);
        meleeInventory.addStack(hammerOrSword == 0 ? new ItemStack(Tools.MITHRIL_SWORD) : new ItemStack(Tools.MITHRIL_HAMMER));
        rangeAttackInventory.addStack(new ItemStack(Items.BOW));

    }

    @Override
    protected int getXpToDrop() {
        //5000xp
        return getXpForLevel(5) * 10;
    }



    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        super.initEquipment(random, localDifficulty);


        this.equipStack(EquipmentSlot.MAINHAND, rangeAttackInventory.getStack(0));
        this.equipStack(EquipmentSlot.CHEST, new ItemStack(Armors.MITHRIL_CHEST_PLATE));
        this.equipStack(EquipmentSlot.FEET, new ItemStack(Armors.MITHRIL_BOOTS));
        this.equipStack(EquipmentSlot.LEGS, new ItemStack(Armors.MITHRIL_LEGGINGS));
        this.equipStack(EquipmentSlot.HEAD, new ItemStack(Armors.MITHRIL_HELMET));
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new AvoidSunlightGoal(this));
        this.goalSelector.add(3, new EscapeSunlightGoal(this, 1.0));
        this.goalSelector.add(3, new FleeEntityGoal<>(this, WolfEntity.class, 6.0F, 1.0, 1.2));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));


        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(3, new BoneLordTargetGoal<>(this, PlayerEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));


    }


    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("shouldSummonArmy", this.shouldSummonArmy);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("shouldSummonArmy")) {
            this.shouldSummonArmy= nbt.getBoolean("shouldSummonArmy");
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if (!this.isRemoved() && !this.dead) {
            Entity entity = damageSource.getAttacker();
            LivingEntity livingEntity = this.getPrimeAdversary();
            if (this.scoreAmount >= 0 && livingEntity != null) {
                livingEntity.updateKilledAdvancementCriterion(this, this.scoreAmount, damageSource);
            }

            if (this.isSleeping()) {
                this.wakeUp();
            }

            if (!this.getWorld().isClient && this.hasCustomName()) {
                OnServerInitialize.LOGGER.info("Named entity {} died: {}", this, this.getDamageTracker().getDeathMessage().getString());
            }

            this.dead = true;
            this.getDamageTracker().update();
            if (this.getWorld() instanceof ServerWorld serverWorld) {
                if (entity == null || entity.onKilledOther(serverWorld, this)) {
                    this.emitGameEvent(GameEvent.ENTITY_DIE);
                    this.drop(serverWorld, damageSource);
                    this.onKilledBy(livingEntity);
                }
                this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            }
            this.setPose(EntityPose.DYING);
        }
    }

    protected void drop(ServerWorld world, DamageSource damageSource) {
        this.dropInventory();
        this.dropXp(damageSource.getAttacker());
    }

    @Override
    public void onAttacking(Entity target) {
        super.onAttacking(target);
        if(target instanceof PlayerEntity player && this.getMainHandStack().isOf(Tools.MITHRIL_HAMMER)){
            player.addStatusEffect(new StatusEffectInstance(SLOWNESS,100,2));
        }
    }


    class BoneLordTargetGoal<T extends LivingEntity> extends TrackTargetGoal {
        protected final Class<T> targetClass;
        /**
         * The reciprocal of chance to actually search for a target on every tick
         * when this goal is not started. This is also the average number of ticks
         * between each search (as in a poisson distribution).
         */
        protected final int reciprocalChance;
        @Nullable
        protected LivingEntity targetEntity;
        protected TargetPredicate targetPredicate;


        public BoneLordTargetGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility) {
            this(mob, targetClass, 10, checkVisibility, false, null);
        }

        public BoneLordTargetGoal(
                MobEntity mob,
                Class<T> targetClass,
                int reciprocalChance,
                boolean checkVisibility,
                boolean checkCanNavigate,
                @Nullable Predicate<LivingEntity> targetPredicate
        ) {
            super(mob, checkVisibility, checkCanNavigate);
            this.targetClass = targetClass;
            this.reciprocalChance = toGoalTicks(reciprocalChance);
            this.setControls(EnumSet.of(Goal.Control.TARGET));
            this.targetPredicate = TargetPredicate.createAttackable().setBaseMaxDistance(this.getFollowRange()).setPredicate(targetPredicate);
        }

        @Override
        public boolean canStart() {
            this.findClosestTarget();
            return this.targetEntity != null;

        }


        private ArrayList<BlockPos> getAvailablePositions(BlockPos mobPos) {
            ArrayList<BlockPos> optionalPos = new ArrayList<>();


            World world = this.mob.getEntityWorld();

            optionalPos.add(mobPos.east());
            optionalPos.add(mobPos.south());
            optionalPos.add(mobPos.west());
            optionalPos.add(mobPos.north());


            ArrayList<BlockPos> availablePos = new ArrayList<>();


            for (BlockPos pos : optionalPos) {


                for (int y = -2; y <= 2; y++) {
                    BlockPos checkPos = pos.add(0, y, 0);
                    boolean canStand = world.getBlockState(checkPos.down()).isFullCube(world, checkPos);
                    boolean enoughSpace = world.getBlockState(checkPos).isAir() && world.getBlockState(checkPos.up()).isAir();
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
                if (this.mob.getWorld() instanceof ServerWorld serverWorld) {
                    LongDeadEntity army = LONG_DEAD.spawn(serverWorld, pos, SpawnReason.MOB_SUMMONED);
                    if (army != null) {
                        army.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 600, 1, false, false));
                        army.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 600, 1,false,false));
                        army.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 600, 1, false,true));
                    }
                }
            }
        }


        protected Box getSearchBox(double distance) {
            return this.mob.getBoundingBox().expand(distance, 4.0, distance);
        }


        protected void findClosestTarget() {
            if (this.targetClass != PlayerEntity.class && this.targetClass != ServerPlayerEntity.class) {
                this.targetEntity = this.mob
                        .getWorld()
                        .getClosestEntity(
                                this.mob.getWorld().getEntitiesByClass(this.targetClass, this.getSearchBox(this.getFollowRange()), livingEntity -> true),
                                this.targetPredicate,
                                this.mob,
                                this.mob.getX(),
                                this.mob.getEyeY(),
                                this.mob.getZ()
                        );
            } else {
                this.targetEntity = this.mob.getWorld().getClosestPlayer(this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
            }
        }

        @Override
        public void start() {
            this.mob.setTarget(this.targetEntity);
            BoneLordEntity mob = (BoneLordEntity) this.mob;
            if (mob.shouldSummonArmy) {
                summonArmy(this.mob.getBlockPos());
                shouldSummonArmy = false;
            }

            super.start();
        }

        @Override
        public void tick() {


            // 检查当前主手物品
            ItemStack currentStack = this.mob.getStackInHand(Hand.MAIN_HAND);
            float distance = this.mob.distanceTo(targetEntity);

            if (this.targetEntity != null) {
                if (distance >= 8) {
                    // 应该使用远程武器
                    ItemStack rangeWeapon = rangeAttackInventory.getStack(0);
                    if (!currentStack.isOf(rangeWeapon.getItem())) {
                        this.mob.setStackInHand(Hand.MAIN_HAND, rangeWeapon);
                    }
                } else {
                    // 应该使用近战武器
                    ItemStack meleeWeapon = meleeInventory.getStack(0);
                    if (!currentStack.isOf(meleeWeapon.getItem())) {
                        this.mob.setStackInHand(Hand.MAIN_HAND, meleeWeapon);
                    }
                }
            }
        }
    }


}
