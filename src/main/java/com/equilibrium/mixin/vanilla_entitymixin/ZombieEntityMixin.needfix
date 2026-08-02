package com.equilibrium.mixin.vanilla_entitymixin;

import com.equilibrium.entity.goal.AdvanceActiveTargetGoal;
import com.equilibrium.entity.goal.BreakBlockGoal;
import com.equilibrium.entity.goal.LookAtTargetGoal;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static com.equilibrium.server_and_client.server.moonphase_tasks.WorldMoonPhasesSelector.calculateMoonType;
import static com.equilibrium.util.XpHashMap.getXpForLevel;

@Mixin(Zombie.class)
public abstract class ZombieEntityMixin extends Monster {
    protected ZombieEntityMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }


//    @Unique
//    @Override
//    public boolean canPickupItem(ItemStack stack) {
//        return stack.isOf(Items.EGG) && this.isBaby() && this.hasVehicle() ? false : true;
//    }
    @Inject(method = "canPickupItem",at = @At(value = "HEAD"),cancellable = true)
    public void canPickupItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

//    @Inject(method = "createZombieAttributes", at = @At(value = "HEAD"))
//    private static void createZombieAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
//        cir.cancel();

//        cir.setReturnValue(HostileEntity.createHostileAttributes()
//                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0)
//                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.30F)
//                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0)
//                .add(EntityAttributes.GENERIC_ARMOR, 2.0)
//                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS));
//    }


    @Inject(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/LookAtEntityGoal;<init>(Lnet/minecraft/entity/mob/MobEntity;Ljava/lang/Class;F)V"))
    protected void initGoals(CallbackInfo ci) {
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, AgeableMob.class, 8.0F,0.02f,true));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F,0.02f,true));
    }

    MobEffectInstance statusEffectInstance = new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 2, false, true, false);






    @Inject(method = "initCustomGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/MoveThroughVillageGoal;<init>(Lnet/minecraft/entity/mob/PathAwareEntity;DZILjava/util/function/BooleanSupplier;)V", shift = At.Shift.AFTER), cancellable = true)
    protected void initCustomGoalss(CallbackInfo ci) {
        ci.cancel();

//        this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));

        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers(ZombifiedPiglin.class));
        //僵尸透视泥土等方块,具体见 AdvanceTargetPredicate
        this.targetSelector.addGoal(2, new AdvanceActiveTargetGoal<>(this, Player.class, false));
        this.goalSelector.addGoal(1, new BreakBlockGoal(this, 800, difficulty -> difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(4, new AdvanceActiveTargetGoal<>(this, AgeableMob.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));


        // 添加盯着目标的目标选择器
        this.goalSelector.addGoal(3, new LookAtTargetGoal(this));
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tick(CallbackInfo ci) {
        if (Objects.equals(calculateMoonType(this.level()), "bloodMoon") && !this.hasEffect(MobEffects.DAMAGE_BOOST)) {
            this.addEffect(statusEffectInstance);
        }
    }
    @Unique
    @Override
    protected Vec3i getPickupReach() {
        return super.getPickupReach().offset(3,0,3);

    }
    @Unique
    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        ItemStack itemStack = itemEntity.getItem();
        if (this.canHoldItem(itemStack)) {
            if (isFood(itemStack)) {
                ItemStack leftover = this.addFoodToInventory(itemStack);
                if (leftover.isEmpty()) {
                    itemEntity.discard();
                    // 播放音效
                    this.level().playSound(null, this.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                } else {
                    itemEntity.setItem(leftover);
                }
            } else {
                super.pickUpItem(itemEntity);
            }
        }
    }

    @Unique
    private ItemStack addFoodToInventory(ItemStack stack) {
        for (int i = 0; i < this.getInventory().getContainerSize(); i++) {
            ItemStack itemStack = this.getInventory().getItem(i);
            if (itemStack.isEmpty()) {
                this.getInventory().setItem(i, stack.copy());
                return ItemStack.EMPTY;
            } else if (ItemStack.isSameItem(itemStack, stack) && itemStack.getCount() < itemStack.getMaxStackSize()) {
                int remaining = itemStack.getMaxStackSize() - itemStack.getCount();
                int countToAdd = Math.min(stack.getCount(), remaining);
                itemStack.grow(countToAdd);
                stack.shrink(countToAdd);
                if (stack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }
        }
        return stack;
    }

    @Override
    public int getBaseExperienceReward(){
        return getXpForLevel(1);
    }



    // 处理僵尸死亡事件
    @Unique
    @Override
    public void die(DamageSource source) {
        super.die(source);
        dropFoodOnDeath();
    }

    @Unique
    private void dropFoodOnDeath() {
        for (int i = 0; i < this.getInventory().getContainerSize(); i++) {
            ItemStack stack = this.getInventory().getItem(i);
            if (isFood(stack)) {
                int dropCount = stack.getCount() / 2;
                if (dropCount > 0) {
                    ItemStack dropStack = stack.copy();
                    dropStack.setCount(dropCount);
                    this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), dropStack));
                    stack.shrink(dropCount);
                }
            }
        }

    }


    @Unique
    private boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.MEAT);
    }

    @Unique
    private final SimpleContainer inventory = new SimpleContainer(10);
    @Unique
    public SimpleContainer getInventory() {
        return inventory;
    }

    @Inject(method = "initialize",at = @At(value = "TAIL"))
    public void initialize(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, SpawnGroupData entityData, CallbackInfoReturnable<SpawnGroupData> cir) {
        this.setCanPickUpLoot(true);

    }











}

