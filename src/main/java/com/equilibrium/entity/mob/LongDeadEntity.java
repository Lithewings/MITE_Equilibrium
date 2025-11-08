package com.equilibrium.entity.mob;

import com.equilibrium.item.Armors;
import com.equilibrium.item.Tools;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registries;
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

import static com.equilibrium.MITEequilibrium.MOD_ID;
import static com.equilibrium.util.XpHashMap.getXpForLevel;

public class LongDeadEntity extends ModAbstractSkeletonEntity {

    public LongDeadEntity(EntityType<? extends ModAbstractSkeletonEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    SoundEvent getStepSound() {
        return SoundEvents.ENTITY_SKELETON_STEP;
    }



    @Override
    protected int getXpToDrop(){
        return getXpForLevel(2);
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

    public BlockPos adjustPosition(WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        return world.getBlockState(blockPos).canPathfindThrough(NavigationType.LAND) ? blockPos : pos;
    }

    private BlockPos findGroundPosition(World world, BlockPos start) {
        // 我们从start开始向下搜索，直到世界底部或者找到合适的地面
        for (int y = start.getY(); start.getY()-y<=32&&y>-64; y--) {
            BlockPos pos = new BlockPos(start.getX(), y, start.getZ());
            if(world.isTopSolid(pos,this))
                return pos.offset(Direction.Axis.Y,1); // 返回这个方块的位置，我们将在其上方生成生物
        }
        // 如果没有找到，返回null
        return null;
    }

    @Override
    public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
        //只在地下世界发现悬空生成的情况,奇怪
        if(!this.isOnGround()){
            BlockPos pos = findGroundPosition(this.getWorld(),this.getBlockPos());
            if(pos!=null)
                this.setPosition(pos.getX(),pos.getY(),pos.getZ());
            else
                return false;
        }

        return super.canSpawn(world, spawnReason);
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
    public void tick() {
        super.tick();
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

