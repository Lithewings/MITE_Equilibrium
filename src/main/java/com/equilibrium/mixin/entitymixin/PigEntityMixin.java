package com.equilibrium.mixin.entitymixin;

import com.equilibrium.entity.goal.AdvanceEscapeDangerGoal;
import com.equilibrium.entity.goal.FleeEntityGoalBesidesPlayer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PigEntity.class)
public abstract class PigEntityMixin extends AnimalEntity implements ItemSteerable, Saddleable {
    protected PigEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }
    @Inject(method = "initGoals",at = @At("HEAD"))
    public void initGoals(CallbackInfo ci) {
        this.goalSelector.add(0, new SwimGoal(this));

        //讨厌玩家
        this.goalSelector.add(4, new FleeEntityGoalBesidesPlayer<>(this, PlayerEntity.class, 3.0F, 1.4, 1.8));
        this.goalSelector.add(1, new AdvanceEscapeDangerGoal(this, 2.25));


        this.goalSelector.add(3, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(4, new TemptGoal(this, 1.6, stack -> stack.isOf(Items.CARROT_ON_A_STICK), false));
        this.goalSelector.add(4, new TemptGoal(this, 1.6, stack -> stack.isIn(ItemTags.PIG_FOOD), false));
        this.goalSelector.add(5, new FollowParentGoal(this, 1.1));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }
}
