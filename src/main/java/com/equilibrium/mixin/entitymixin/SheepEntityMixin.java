package com.equilibrium.mixin.entitymixin;

import com.equilibrium.entity.goal.AdvanceEscapeDangerGoal;
import com.equilibrium.entity.goal.FleeEntityGoalBesidesPlayer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SheepEntity.class)
public abstract  class SheepEntityMixin extends AnimalEntity implements Shearable {


    protected SheepEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    private EatGrassGoal eatGrassGoal;
    @Inject(method = "initGoals",at = @At("HEAD"), cancellable = true)
    public void initGoals(CallbackInfo ci) {
        ci.cancel();
        this.eatGrassGoal = new EatGrassGoal((SheepEntity)(Object)this);
        this.goalSelector.add(0, new SwimGoal((SheepEntity)(Object)this));

        //讨厌玩家
        this.goalSelector.add(4, new FleeEntityGoalBesidesPlayer<>(this, PlayerEntity.class, 3.0F, 1.4, 1.8));
        this.goalSelector.add(1, new AdvanceEscapeDangerGoal(this, 2.25));

        this.goalSelector.add(2, new AnimalMateGoal((SheepEntity)(Object)this, 1.0));
        this.goalSelector.add(3, new TemptGoal((SheepEntity)(Object)this, 1.6, stack -> stack.isIn(ItemTags.SHEEP_FOOD), false));
        this.goalSelector.add(4, new FollowParentGoal((SheepEntity)(Object)this, 1.1));
        this.goalSelector.add(5, this.eatGrassGoal);
        this.goalSelector.add(6, new WanderAroundFarGoal((SheepEntity)(Object)this, 1.0));
        this.goalSelector.add(7, new LookAtEntityGoal((SheepEntity)(Object)this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal((SheepEntity)(Object)this));
    }






}
