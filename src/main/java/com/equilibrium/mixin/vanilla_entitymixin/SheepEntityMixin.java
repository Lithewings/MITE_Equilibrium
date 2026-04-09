package com.equilibrium.mixin.vanilla_entitymixin;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.entity.EnvironmentChecker;
import com.equilibrium.entity.ProduceManureOrSomething;
import com.equilibrium.entity.goal.ConstantFleePlayerGoal;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
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

@Mixin(SheepEntity.class)
public abstract  class SheepEntityMixin extends AnimalEntity implements Shearable , ProduceManureOrSomething {


    protected SheepEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }



    @Inject(method = "<init>",at = @At("TAIL"))
    public void init(EntityType<?>entityType, World world, CallbackInfo ci){
        if(this.getWorld() instanceof ServerWorld serverWorld){
            boolean shouldNotGen = getGameBooleanRuleFromServer(ENABLE_NO_ANIMALS,serverWorld.getServer());
            if(shouldNotGen){
                this.discard();
            }
        }
    }





    @Unique
    private final EnvironmentChecker environmentChecker =new EnvironmentChecker((SheepEntity)(Object)this,6000);


    @Inject(method = "mobTick",at = @At("HEAD"))
    protected void mobTick(CallbackInfo ci) {
        environmentChecker.tickTask();
    }



    @Inject(method = "interactMob",at = @At("HEAD"), cancellable = true)
    public void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if(player.getWorld().isClient()) {
            cir.setReturnValue(ActionResult.SUCCESS);
            return;
        }






        if(environmentChecker.isIllness()){
            player.sendMessage(Text.of("The sheep can not be interact due to the illness."));
            environmentChecker.interactTask(player);
            cir.setReturnValue(ActionResult.PASS);
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
    @Inject(method = "tickMovement",at = @At("HEAD"))
    public void tickMovement(CallbackInfo ci) {
        this.produceManure(this);
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


    @Inject(method = "writeCustomDataToNbt",at = @At("TAIL"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        environmentChecker.writeCustomDataToNbt(nbt);
    }

    @Inject(method = "readCustomDataFromNbt",at = @At("TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        environmentChecker.readCustomDataFromNbt(nbt);
    }
    @Shadow
    private EatGrassGoal eatGrassGoal;

    @Override
    public void initGoals() {
        this.eatGrassGoal = new EatGrassGoal(this);
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(0, new AnimalMateGoal(this, 1.0));


        //讨厌玩家
        this.goalSelector.add(1, new ConstantFleePlayerGoal(this, 8.0F, 1.7, 1.8));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 2));


        this.goalSelector.add(3, new TemptGoal(this, 1.5, stack -> stack.isIn(ItemTags.SHEEP_FOOD), false));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.add(5, this.eatGrassGoal);
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
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
                    this.onKilledBy(livingEntity);
                }

                this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            }

            this.setPose(EntityPose.DYING);
        }
    }


}
