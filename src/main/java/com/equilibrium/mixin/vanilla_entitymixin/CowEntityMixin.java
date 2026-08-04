package com.equilibrium.mixin.vanilla_entitymixin;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.entity.EnvironmentChecker;
import com.equilibrium.entity.ProduceManureOrSomething;
import com.equilibrium.entity.goal.BreakGrassGoal;
import com.equilibrium.entity.goal.ConstantFleePlayerGoal;
import com.equilibrium.item.food.FoodOrFarmItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_NO_ANIMALS;


@Mixin(Cow.class)
public abstract class CowEntityMixin extends Animal implements ProduceManureOrSomething {

    protected CowEntityMixin(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
    }

    @Unique
    private int milkCoolDown =0;


    @Override
    public boolean checkSpawnRules(LevelAccessor world, MobSpawnType spawnReason) {
        if(world instanceof ServerLevel){
            boolean shouldNotGen = getGameBooleanRuleFromServer(ENABLE_NO_ANIMALS,world.getServer());
            if(spawnReason==MobSpawnType.NATURAL && shouldNotGen){
                return false;
            }
            return true;
        }
        return super.checkSpawnRules(world,spawnReason);
    }



    @Inject(method = "createAttributes",at = @At("HEAD"),cancellable = true)
    private static void createCowAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        cir.setReturnValue(Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0).add(Attributes.MOVEMENT_SPEED, 0.2F));
    }


    @Unique
    private final EnvironmentChecker environmentChecker =new EnvironmentChecker((Cow)(Object)this,6000);


    @Override
    public void aiStep() {
        super.aiStep();
        if(!environmentChecker.isIllness())
            produceManure(this);
    }

    @Unique
    public int itemLayTime =  this.random.nextInt(6000)+6000;


    @Unique
    public void produceManure(Animal entity) {
        if (--this.itemLayTime <= 0) {
            ProduceManureOrSomething.produceManure(entity);
            this.itemLayTime = this.random.nextInt(6000)+6000;
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
                    if(!environmentChecker.isIllness())
                        this.dropAllDeathLoot(serverWorld, damageSource);
                    this.createWitherRose(livingEntity);
                }

                this.level().broadcastEntityEvent(this, EntityEvent.DEATH);
            }

            this.setPose(Pose.DYING);
        }
    }


    @Inject(method = "mobInteract",at = @At("HEAD"), cancellable = true)
    public void interactMob(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        //把原版挤奶逻辑cancel掉
        cir.cancel();

        if (player.level().isClientSide()) {
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }

        if(environmentChecker.isIllness()){
            player.sendSystemMessage(Component.nullToEmpty("The cow can not be interact due to the illness."));
            environmentChecker.interactTask(player);
            cir.setReturnValue(InteractionResult.PASS);
            //在这里停下
            return;
        }




        ItemStack itemStack = player.getItemInHand(hand);

        if(itemStack.is(Items.BUCKET)&&this.isBaby()){
            //原版逻辑
            player.sendSystemMessage(Component.nullToEmpty("The baby cow can not be milked."));
            cir.setReturnValue(super.mobInteract(player, hand));
            //在这里停下
            return;
        }


        if (this.milkCoolDown<=0 && itemStack.is(Items.BUCKET) && !this.isBaby()) {
            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
            ItemStack itemStack2 = ItemUtils.createFilledResult(itemStack, player, Items.MILK_BUCKET.getDefaultInstance());
            player.setItemInHand(hand, itemStack2);
            this.milkCoolDown = 24000;


        }
        else if(this.milkCoolDown>=0 && this.milkCoolDown<18000 && itemStack.is(Items.BOWL) &&!this.isBaby()){
            ItemStack itemStack2 = ItemUtils.createFilledResult(itemStack, player, FoodOrFarmItems.MILK_BOWL.getDefaultInstance());
            player.setItemInHand(hand, itemStack2);
            milkCoolDown += 6000;


        } else if (this.milkCoolDown>=0  && itemStack.is(Items.BUCKET)||itemStack.is(Items.BOWL)) {
            player.sendSystemMessage(Component.nullToEmpty("The cow did not prepare for milking."));

        }

        environmentChecker.interactTask(player);
        //后续原版逻辑...
        cir.setReturnValue(super.mobInteract(player, hand));
    }



    @Override
    public void customServerAiStep() {
        environmentChecker.tickTask();
        if(!this.level().isClientSide()) {
            this.milkCoolDown--;
            if(milkCoolDown<=0)
                milkCoolDown=0;
        }
    }



    @Inject(method = "registerGoals",at = @At(value = "HEAD"),cancellable = true)
    protected void initGoals(CallbackInfo ci) {
        ci.cancel();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ConstantFleePlayerGoal(this, 8.0F, 1.7, 1.8));
        this.goalSelector.addGoal(2, new PanicGoal(this, 2));

        this.goalSelector.addGoal(0, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.6, stack -> stack.is(ItemTags.COW_FOOD), false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(10, new BreakGrassGoal((Cow)(Object)this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }


    @Override
    @Nullable
    public DamageSource getLastDamageSource() {
        LivingEntityAccessor accessor = (LivingEntityAccessor) this;
        if (this.level().getGameTime() - accessor.getPrivateLastDamageStamp() > 1600L) {
            accessor.setPrivateLastDamageSource(null);
        }
        return accessor.getPrivateLastDamageSource();
    }


    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        environmentChecker.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        environmentChecker.readCustomDataFromNbt(nbt);
    }







}

