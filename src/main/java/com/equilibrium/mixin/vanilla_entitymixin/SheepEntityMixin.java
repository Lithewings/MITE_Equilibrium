package com.equilibrium.mixin.vanilla_entitymixin;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.entity.EnvironmentChecker;
import com.equilibrium.entity.ProduceManureOrSomething;
import com.equilibrium.entity.goal.ConstantFleePlayerGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_NO_ANIMALS;

@Mixin(Sheep.class)
public abstract  class SheepEntityMixin extends Animal implements Shearable , ProduceManureOrSomething {


    protected SheepEntityMixin(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
    }



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




    @Unique
    private final EnvironmentChecker environmentChecker =new EnvironmentChecker((Sheep)(Object)this,6000);


    @Inject(method = "customServerAiStep",at = @At("HEAD"))
    protected void mobTick(CallbackInfo ci) {
        environmentChecker.tickTask();
    }



    @Inject(method = "mobInteract",at = @At("HEAD"), cancellable = true)
    public void interactMob(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if(player.level().isClientSide()) {
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }






        if(environmentChecker.isIllness()){
            player.sendSystemMessage(Component.nullToEmpty("The sheep can not be interact due to the illness."));
            environmentChecker.interactTask(player);
            cir.setReturnValue(InteractionResult.PASS);
            //在这里停下
            return;
        }
        environmentChecker.interactTask(player);
        //后续原版逻辑...
    }

    //如果使用override,会直接重载父类的方法,而原先mixin的重载方法代码段:
    // if (this.getWorld().isClient) {
    //			this.eatGrassTimer = Math.max(0, this.eatGrassTimer - 1);
    //		}
    //将被忽视
    @Inject(method = "aiStep",at = @At("HEAD"))
    public void tickMovement(CallbackInfo ci) {
        this.produceManure(this);
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


    @Inject(method = "addAdditionalSaveData",at = @At("TAIL"))
    public void writeCustomDataToNbt(CompoundTag nbt, CallbackInfo ci) {
        environmentChecker.writeCustomDataToNbt(nbt);
    }

    @Inject(method = "readAdditionalSaveData",at = @At("TAIL"))
    public void readCustomDataFromNbt(CompoundTag nbt, CallbackInfo ci) {
        environmentChecker.readCustomDataFromNbt(nbt);
    }
    @Shadow
    private EatBlockGoal eatBlockGoal;

    @Override
    public void registerGoals() {
        this.eatBlockGoal = new EatBlockGoal(this);
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new BreedGoal(this, 1.0));


        //讨厌玩家
        this.goalSelector.addGoal(1, new ConstantFleePlayerGoal(this, 8.0F, 1.7, 1.8));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2));


        this.goalSelector.addGoal(3, new TemptGoal(this, 1.5, stack -> stack.is(ItemTags.SHEEP_FOOD), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(5, this.eatBlockGoal);
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }



    @Override
    public void die(DamageSource damageSource) {
        onDeathAndCheckIllness(damageSource);
    }

    @Unique
    private void onDeathAndCheckIllness(DamageSource damageSource) {
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


}
