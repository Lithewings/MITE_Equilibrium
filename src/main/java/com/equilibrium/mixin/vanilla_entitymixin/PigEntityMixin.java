package com.equilibrium.mixin.vanilla_entitymixin;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.entity.EnvironmentChecker;
import com.equilibrium.entity.ProduceManureOrSomething;
import com.equilibrium.entity.goal.ConstantFleePlayerGoal;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_NO_ANIMALS;

@Mixin(PigEntity.class)
public abstract class PigEntityMixin extends AnimalEntity implements ItemSteerable, Saddleable , ProduceManureOrSomething {
    @Shadow public abstract boolean isSaddled();

    protected PigEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }


    @Override
    public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
        if(world.getServer()!=null && getGameBooleanRuleFromServer(ENABLE_NO_ANIMALS, world.getServer()))
            return false;
        return super.canSpawn(world,spawnReason);
    }



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





    @Inject(method = "initGoals",at = @At("HEAD"))
    public void initGoals(CallbackInfo ci) {
        this.goalSelector.add(0, new SwimGoal(this));

        //讨厌玩家
        this.goalSelector.add(1, new ConstantFleePlayerGoal(this, 8.0F, 1.7, 1.8));
        this.goalSelector.add(2, new EscapeDangerGoal(this, 2));


        this.goalSelector.add(0, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(4, new TemptGoal(this, 1.6, stack -> stack.isOf(Items.CARROT_ON_A_STICK), false));
        this.goalSelector.add(4, new TemptGoal(this, 1.6, stack -> stack.isIn(ItemTags.PIG_FOOD), false));
        this.goalSelector.add(5, new FollowParentGoal(this, 1.1));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }


    @Unique
    private final EnvironmentChecker environmentChecker = new EnvironmentChecker((PigEntity)(Object)this,6000);

    @Override
    protected void mobTick() {
        super.mobTick();
        environmentChecker.tickTask();
    }

    @Inject(method = "interactMob",at = @At("HEAD"), cancellable = true)
    public void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if(player.getWorld().isClient()) {
            cir.setReturnValue(ActionResult.SUCCESS);
            return;
        }

        if(environmentChecker.isIllness()){
            player.sendMessage(Text.of("The pig can not be interact due to the illness."));
            environmentChecker.interactTask(player);
            cir.setReturnValue(ActionResult.PASS);
            //在这里停下
            return;
        }
        environmentChecker.interactTask(player);
        //后续原版逻辑...
    }
    @Inject(method = "writeCustomDataToNbt",at = @At("TAIL"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        environmentChecker.writeCustomDataToNbt(nbt);
    }

    @Inject(method = "readCustomDataFromNbt",at = @At("TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        environmentChecker.readCustomDataFromNbt(nbt);
    }


    @Override
    public void onDeath(DamageSource damageSource) {
        onDeathAndCheckIllness(damageSource);
    }

    @Unique
    private void onDeathAndCheckIllness(DamageSource damageSource) {
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
                    else if(this.isSaddled()){
                        this.getWorld().spawnEntity(new ItemEntity(this.getWorld(),this.getX(),this.getY(),this.getZ(),Items.SADDLE.getDefaultStack()));
                    }
                    this.onKilledBy(livingEntity);
                }

                this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            }

            this.setPose(EntityPose.DYING);
        }
    }

}
