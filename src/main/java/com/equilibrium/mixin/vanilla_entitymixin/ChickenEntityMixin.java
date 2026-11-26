package com.equilibrium.mixin.vanilla_entitymixin;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.entity.EnvironmentChecker;
import com.equilibrium.entity.ProduceManureOrSomething;
import com.equilibrium.entity.goal.ConstantFleePlayerGoal;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_NO_ANIMALS;

@Mixin(ChickenEntity.class)
public abstract class ChickenEntityMixin extends AnimalEntity implements ProduceManureOrSomething {
    @Shadow public float prevFlapProgress;

    protected ChickenEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
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
        if(!environmentChecker.isIllness()){
            produceManureAndFeather(this);

        }
    }

    @Unique
    public int itemLayTime =  this.random.nextInt(6000)+6000;


    @Unique
    public void produceManureAndFeather(AnimalEntity entity) {
        if (--this.itemLayTime <= 0) {
            ProduceManureOrSomething.produceManure(entity);
            ProduceManureOrSomething.produceFeather(entity);
            this.itemLayTime = this.random.nextInt(6000)+6000;
        }
    }

    @Inject(method = "initGoals",at = @At("HEAD"), cancellable = true)
    public void initGoals(CallbackInfo ci) {
        ci.cancel();
        this.goalSelector.add(0, new SwimGoal(this));

        //讨厌玩家
        this.goalSelector.add(1, new ConstantFleePlayerGoal(this, 8.0F, 1.7, 1.8));
        this.goalSelector.add(2, new EscapeDangerGoal(this, 2));


        this.goalSelector.add(0, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(4, new TemptGoal(this, 1.6, stack -> stack.isIn(ItemTags.CHICKEN_FOOD), false));
        this.goalSelector.add(5, new FollowParentGoal(this, 1.1));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }

    @Unique
    private final EnvironmentChecker environmentChecker = new EnvironmentChecker((ChickenEntity)(Object)this,6000);

    @Override
    protected void mobTick() {
        super.mobTick();
        environmentChecker.tickTask();
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if(player.getWorld().isClient()) {
            return ActionResult.SUCCESS;
        }

        if(environmentChecker.isIllness()){
            player.sendMessage(Text.of("The chicken can not be interact due to the illness."));
            environmentChecker.interactTask(player);
            //在这里停下
            return ActionResult.PASS;
        }
        environmentChecker.interactTask(player);
        //后续原版逻辑...
        return super.interactMob(player,hand);
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
    protected void drop(ServerWorld world, DamageSource damageSource) {
        if(environmentChecker.isIllness())
            return;
        boolean bl = this.playerHitTimer > 0;
        if (this.shouldDropLoot() && world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.dropLoot(damageSource, bl);
            this.dropEquipment(world, damageSource, bl);
        }
        this.dropInventory();
        this.dropXp(damageSource.getAttacker());
    }



}
