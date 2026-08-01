package com.equilibrium.mixin.vanilla_entitymixin;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.entity.EnvironmentChecker;
import com.equilibrium.entity.ProduceManureOrSomething;
import com.equilibrium.entity.goal.BreakGrassGoal;
import com.equilibrium.entity.goal.ConstantFleePlayerGoal;

import com.equilibrium.item.food.FoodOrFarmItems;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;

import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_NO_ANIMALS;


@Mixin(CowEntity.class)
public abstract class CowEntityMixin extends AnimalEntity implements ProduceManureOrSomething {

    protected CowEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private int milkCoolDown =0;


    @Override
    public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
        if(world.getServer()!=null && getGameBooleanRuleFromServer(ENABLE_NO_ANIMALS, world.getServer()))
            return false;
        return super.canSpawn(world,spawnReason);
    }



    @Inject(method = "createCowAttributes",at = @At("HEAD"),cancellable = true)
    private static void createCowAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.setReturnValue(MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2F));
    }


    @Unique
    private final EnvironmentChecker environmentChecker =new EnvironmentChecker((CowEntity)(Object)this,6000);


    @Override
    public void tickMovement() {
        super.tickMovement();
        if(!environmentChecker.isIllness())
            produceManure(this);
    }

    @Unique
    public int itemLayTime =  this.random.nextInt(6000)+6000;


    @Unique
    public void produceManure(AnimalEntity entity) {
        if (--this.itemLayTime <= 0) {
            ProduceManureOrSomething.produceManure(entity);
            this.itemLayTime = this.random.nextInt(6000)+6000;
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
                    if(!environmentChecker.isIllness())
                        this.drop(serverWorld, damageSource);
                    this.onKilledBy(livingEntity);
                }

                this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            }

            this.setPose(EntityPose.DYING);
        }
    }


    @Inject(method = "interactMob",at = @At("HEAD"), cancellable = true)
    public void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        //把原版挤奶逻辑cancel掉
        cir.cancel();

        if (player.getWorld().isClient()) {
            cir.setReturnValue(ActionResult.SUCCESS);
            return;
        }

        if(environmentChecker.isIllness()){
            player.sendMessage(Text.of("The cow can not be interact due to the illness."));
            environmentChecker.interactTask(player);
            cir.setReturnValue(ActionResult.PASS);
            //在这里停下
            return;
        }




        ItemStack itemStack = player.getStackInHand(hand);

        if(itemStack.isOf(Items.BUCKET)&&this.isBaby()){
            //原版逻辑
            player.sendMessage(Text.of("The baby cow can not be milked."));
            cir.setReturnValue(super.interactMob(player, hand));
            //在这里停下
            return;
        }


        if (this.milkCoolDown<=0 && itemStack.isOf(Items.BUCKET) && !this.isBaby()) {
            player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
            ItemStack itemStack2 = ItemUsage.exchangeStack(itemStack, player, Items.MILK_BUCKET.getDefaultStack());
            player.setStackInHand(hand, itemStack2);
            this.milkCoolDown = 24000;


        }
        else if(this.milkCoolDown>=0 && this.milkCoolDown<18000 && itemStack.isOf(Items.BOWL) &&!this.isBaby()){
            ItemStack itemStack2 = ItemUsage.exchangeStack(itemStack, player, FoodOrFarmItems.MILK_BOWL.getDefaultStack());
            player.setStackInHand(hand, itemStack2);
            milkCoolDown += 6000;


        } else if (this.milkCoolDown>=0  && itemStack.isOf(Items.BUCKET)||itemStack.isOf(Items.BOWL)) {
            player.sendMessage(Text.of("The cow did not prepare for milking."));

        }

        environmentChecker.interactTask(player);
        //后续原版逻辑...
        cir.setReturnValue(super.interactMob(player, hand));
    }



    @Override
    public void mobTick() {
        environmentChecker.tickTask();
        if(!this.getWorld().isClient()) {
            this.milkCoolDown--;
            if(milkCoolDown<=0)
                milkCoolDown=0;
        }
    }



    @Inject(method = "initGoals",at = @At(value = "HEAD"),cancellable = true)
    protected void initGoals(CallbackInfo ci) {
        ci.cancel();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new ConstantFleePlayerGoal(this, 8.0F, 1.7, 1.8));
        this.goalSelector.add(2, new EscapeDangerGoal(this, 2));

        this.goalSelector.add(0, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(3, new TemptGoal(this, 1.6, stack -> stack.isIn(ItemTags.COW_FOOD), false));
        this.goalSelector.add(5, new FollowParentGoal(this, 1.25));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(10, new BreakGrassGoal((CowEntity)(Object)this));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }
    @Unique
    @Override
    public DamageSource getRecentDamageSource() {
        if (this.getWorld().getTime() -this.lastDamageTime > 1600L) {
            this.lastDamageSource = null;
        }
        return this.lastDamageSource;
    }



    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        environmentChecker.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        environmentChecker.readCustomDataFromNbt(nbt);
    }







}

