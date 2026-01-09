package com.equilibrium.entity.mob;


import com.equilibrium.entity.goal.MeleeAttackGoalApplyAttackRange;
import com.equilibrium.item.Tools;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import static com.equilibrium.util.XpHashMap.getXpForLevel;

public class RevenantEntity extends ZombieEntity {
    private final int hammerOrSword;
    public RevenantEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
        this.hammerOrSword = this.getRandom().nextInt(2);
    }

    @Override
    public void setBaby(boolean baby) {
        //ignore
    }

    @Override
    protected int getXpToDrop(){
        return getXpForLevel(3);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.initThisCustomGoals();
    }
    @Override
    protected boolean canConvertInWater() {
        return false;
    }
    protected void initThisCustomGoals() {
        this.goalSelector.add(2, new ZombieAttackGoal(this, 1.0, false));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge(ZombifiedPiglinEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new MeleeAttackGoalApplyAttackRange(this,1.0,false,this.hammerOrSword==0 ?2f:1.5f));

    }
    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        super.initEquipment(random, localDifficulty);
        this.equipStack(EquipmentSlot.MAINHAND, this.hammerOrSword==0 ? new ItemStack(Tools.IRON_SWORD): new ItemStack(Tools.IRON_HAMMER));

        this.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
        this.equipStack(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
        this.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
        this.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
    }

    @Override
    public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
        if(this.getY()>0)
            return false;
        return super.canSpawn(world, spawnReason);
    }
}
