package com.equilibrium.mixin.entitymixin;

import com.equilibrium.entity.goal.AdvanceActiveTargetGoal;
import com.equilibrium.entity.goal.BreakBlockGoal;
import com.equilibrium.entity.goal.LookAtTargetGoal;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static com.equilibrium.util.WorldMoonPhasesSelector.calculateMoonType;
import static com.equilibrium.util.XpHashMap.getXpForLevel;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends HostileEntity {
    protected ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
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
        this.goalSelector.add(8, new LookAtEntityGoal(this, PassiveEntity.class, 8.0F,0.02f,true));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F,0.02f,true));
    }

    StatusEffectInstance statusEffectInstance = new StatusEffectInstance(StatusEffects.STRENGTH, -1, 2, false, true, false);






    @Inject(method = "initCustomGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/MoveThroughVillageGoal;<init>(Lnet/minecraft/entity/mob/PathAwareEntity;DZILjava/util/function/BooleanSupplier;)V", shift = At.Shift.AFTER), cancellable = true)
    protected void initCustomGoalss(CallbackInfo ci) {
        ci.cancel();
//        this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, false));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));

        this.targetSelector.add(3, new RevengeGoal(this).setGroupRevenge(ZombifiedPiglinEntity.class));
        //僵尸透视泥土等方块,具体见 AdvanceTargetPredicate
        this.targetSelector.add(2, new AdvanceActiveTargetGoal<>(this, PlayerEntity.class, false));
        this.goalSelector.add(1, new BreakBlockGoal(this, 800, difficulty -> difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD));
        this.targetSelector.add(4, new ActiveTargetGoal(this, MerchantEntity.class, false));
        this.targetSelector.add(4, new ActiveTargetGoal(this, IronGolemEntity.class, true));
        this.targetSelector.add(4, new AdvanceActiveTargetGoal<>(this, PassiveEntity.class, true));
        this.targetSelector.add(5, new ActiveTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));


        // 添加盯着目标的目标选择器
        this.goalSelector.add(3, new LookAtTargetGoal(this));
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tick(CallbackInfo ci) {
        if (Objects.equals(calculateMoonType(this.getWorld()), "bloodMoon") && !this.hasStatusEffect(StatusEffects.STRENGTH)) {
            this.addStatusEffect(statusEffectInstance);
        }
    }
    @Unique
    @Override
    protected Vec3i getItemPickUpRangeExpander() {
        return super.getItemPickUpRangeExpander().add(3,0,3);

    }
    @Unique
    @Override
    protected void loot(ItemEntity itemEntity) {
        ItemStack itemStack = itemEntity.getStack();
        if (this.canPickupItem(itemStack)) {
            if (isFood(itemStack)) {
                ItemStack leftover = this.addFoodToInventory(itemStack);
                if (leftover.isEmpty()) {
                    itemEntity.discard();
                    // 播放音效
                    this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1.0F, 1.0F);
                } else {
                    itemEntity.setStack(leftover);
                }
            } else {
                super.loot(itemEntity);
            }
        }
    }

    @Unique
    private ItemStack addFoodToInventory(ItemStack stack) {
        for (int i = 0; i < this.getInventory().size(); i++) {
            ItemStack itemStack = this.getInventory().getStack(i);
            if (itemStack.isEmpty()) {
                this.getInventory().setStack(i, stack.copy());
                return ItemStack.EMPTY;
            } else if (ItemStack.areItemsEqual(itemStack, stack) && itemStack.getCount() < itemStack.getMaxCount()) {
                int remaining = itemStack.getMaxCount() - itemStack.getCount();
                int countToAdd = Math.min(stack.getCount(), remaining);
                itemStack.increment(countToAdd);
                stack.decrement(countToAdd);
                if (stack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }
        }
        return stack;
    }

    @Override
    public int getXpToDrop(){
        return getXpForLevel(1);
    }



    // 处理僵尸死亡事件
    @Unique
    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        dropFoodOnDeath();
    }

    @Unique
    private void dropFoodOnDeath() {
        for (int i = 0; i < this.getInventory().size(); i++) {
            ItemStack stack = this.getInventory().getStack(i);
            if (isFood(stack)) {
                int dropCount = stack.getCount() / 2;
                if (dropCount > 0) {
                    ItemStack dropStack = stack.copy();
                    dropStack.setCount(dropCount);
                    this.getWorld().spawnEntity(new ItemEntity(this.getWorld(), this.getX(), this.getY(), this.getZ(), dropStack));
                    stack.decrement(dropCount);
                }
            }
        }

    }


    @Unique
    private boolean isFood(ItemStack stack) {
        return stack.isIn(ItemTags.MEAT);
    }

    @Unique
    private final SimpleInventory inventory = new SimpleInventory(10);
    @Unique
    public SimpleInventory getInventory() {
        return inventory;
    }

    @Inject(method = "initialize",at = @At(value = "TAIL"))
    public void initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, CallbackInfoReturnable<EntityData> cir) {
        this.setCanPickUpLoot(true);

    }











}

