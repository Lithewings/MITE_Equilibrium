package com.equilibrium.entity.mob;

import com.equilibrium.entity.goal.MeleeAttackGoalApplyAttackRange;
import com.equilibrium.item.Armors;
import com.equilibrium.item.Tools;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

import static com.equilibrium.OnServerInitialize.MOD_ID;
import static com.equilibrium.util.XpHashMap.getXpForLevel;

public class LongDeadEntity extends ModAbstractSkeletonEntity {

    public LongDeadEntity(EntityType<? extends ModAbstractSkeletonEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.ENTITY_SKELETON_STEP;
    }



    @Override
    protected int getXpToDrop(){
        return getXpForLevel(2);
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
        persistentProjectileEntity.setVelocity(d, e + g * 0.2F, f, 1.6F, 0f);
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.getWorld().spawnEntity(persistentProjectileEntity);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);

    }

    @Override
    protected RegistryKey<LootTable> getLootTableId() {
        return RegistryKey.of(RegistryKeys.LOOT_TABLE,Identifier.of(MOD_ID,"entity/longdead"));
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
    }



    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new AvoidSunlightGoal(this));
        this.goalSelector.add(3, new EscapeSunlightGoal(this, 1.0));
        this.goalSelector.add(3, new FleeEntityGoal(this, WolfEntity.class, 6.0F, 1.0, 1.2));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));





        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal(this, IronGolemEntity.class, true));

    }


    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        super.initEquipment(random, localDifficulty);
        if(this.getRandom().nextInt(2)==0)
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        else
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Tools.IRON_DAGGER));
        this.equipStack(EquipmentSlot.CHEST, new ItemStack(Armors.ANCIENT_METAL_CHAINMAIL_CHEST_PLATE));
        this.equipStack(EquipmentSlot.FEET, new ItemStack(Armors.ANCIENT_METAL_CHAINMAIL_BOOTS));
        this.equipStack(EquipmentSlot.LEGS, new ItemStack(Armors.ANCIENT_METAL_CHAINMAIL_LEGGINGS));
        this.equipStack(EquipmentSlot.HEAD, new ItemStack(Armors.ANCIENT_METAL_CHAINMAIL_HELMET));
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData);
        Random random = world.getRandom();
        this.initEquipment(random, difficulty);
        this.updateEnchantments(world, random, difficulty);
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
        return SoundEvents.ENTITY_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SKELETON_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    @Override
    //同类不会伤害到自己
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if(damageSource.getAttacker() instanceof LongDeadEntity)
            return true;
        return super.isInvulnerableTo(damageSource);
    }





    @Override
    protected void dropEquipment(ServerWorld world, DamageSource source, boolean causedByPlayer) {
        super.dropEquipment(world, source, causedByPlayer);
        if (source.getAttacker() instanceof CreeperEntity creeperEntity && creeperEntity.shouldDropHead()) {
            creeperEntity.onHeadDropped();
            this.dropItem(Items.SKELETON_SKULL);
        }
    }
}

