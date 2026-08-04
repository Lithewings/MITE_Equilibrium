package com.equilibrium.entity.mob;

import com.equilibrium.item.Armors;
import com.equilibrium.item.Tools;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import static com.equilibrium.OnServerInitialize.MOD_ID;
import static com.equilibrium.util.XpHashMap.getXpForLevel;

public class LongDeadEntity extends ModAbstractSkeletonEntity {

    public LongDeadEntity(EntityType<? extends ModAbstractSkeletonEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.SKELETON_STEP;
    }



    @Override
    protected int getBaseExperienceReward(){
        return getXpForLevel(2);
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
        persistentProjectileEntity.shoot(d, e + g * 0.2F, f, 1.6F, 0f);
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(persistentProjectileEntity);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

    }

    @Override
    protected ResourceKey<LootTable> getDefaultLootTable() {
        return ResourceKey.create(Registries.LOOT_TABLE,ResourceLocation.fromNamespaceAndPath(MOD_ID,"entity/longdead"));
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
    }



    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0));
        this.goalSelector.addGoal(3, new AvoidEntityGoal(this, Wolf.class, 6.0F, 1.0, 1.2));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));





        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));

    }


    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance localDifficulty) {
        super.populateDefaultEquipmentSlots(random, localDifficulty);
        if(this.getRandom().nextInt(2)==0)
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        else
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Tools.IRON_DAGGER.get()));
        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Armors.ANCIENT_METAL_CHAINMAIL_CHEST_PLATE));
        this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Armors.ANCIENT_METAL_CHAINMAIL_BOOTS));
        this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Armors.ANCIENT_METAL_CHAINMAIL_LEGGINGS));
        this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Armors.ANCIENT_METAL_CHAINMAIL_HELMET));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData) {
        entityData = super.finalizeSpawn(world, difficulty, spawnReason, entityData);
        RandomSource random = world.getRandom();
        this.populateDefaultEquipmentSlots(random, difficulty);
        this.populateDefaultEquipmentEnchantments(world, random, difficulty);
        this.updateAttackType();
        this.setCanPickUpLoot(true);
        return entityData;
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    @Override
    //同类不会伤害到自己
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if(damageSource.getEntity() instanceof LongDeadEntity)
            return true;
        return super.isInvulnerableTo(damageSource);
    }





    @Override
    protected void dropCustomDeathLoot(ServerLevel world, DamageSource source, boolean causedByPlayer) {
        super.dropCustomDeathLoot(world, source, causedByPlayer);
        if (source.getEntity() instanceof Creeper creeperEntity && creeperEntity.canDropMobsSkull()) {
            creeperEntity.increaseDroppedSkulls();
            this.spawnAtLocation(Items.SKELETON_SKULL);
        }
    }
}

