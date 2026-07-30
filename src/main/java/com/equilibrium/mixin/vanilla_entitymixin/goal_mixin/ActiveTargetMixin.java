package com.equilibrium.mixin.vanilla_entitymixin.goal_mixin;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.spongepowered.asm.mixin.Mixin;

import static com.equilibrium.OnServerInitialize.MOD_ID;
import static com.equilibrium.server_and_client.server.moonphase_tasks.MoonPhaseEvent.getMoonType;

@Mixin(NearestAttackableTargetGoal.class)
public abstract class ActiveTargetMixin <T extends LivingEntity> extends TargetGoal {
    public ActiveTargetMixin(Mob mob, boolean checkVisibility) {
        super(mob, checkVisibility);
    }
    @Override
    public double getFollowDistance(){
        double range = getMoonType(mob.level()).equals("bloodMoon")? 256:64;
        //不在主世界也依然是64
        if(mob.level().dimension()== ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(MOD_ID, "underworld")))
            range=range*0.75;
        return range;
    }


}
