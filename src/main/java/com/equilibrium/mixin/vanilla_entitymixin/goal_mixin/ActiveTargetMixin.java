package com.equilibrium.mixin.vanilla_entitymixin.goal_mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;

import static com.equilibrium.OnServerInitialize.MOD_ID;
import static com.equilibrium.server_and_client.server.moonphase_tasks.MoonPhaseEvent.getMoonType;

@Mixin(ActiveTargetGoal.class)
public abstract class ActiveTargetMixin <T extends LivingEntity> extends TrackTargetGoal {
    public ActiveTargetMixin(MobEntity mob, boolean checkVisibility) {
        super(mob, checkVisibility);
    }
    @Override
    public double getFollowRange(){
        double range = getMoonType(mob.getWorld()).equals("bloodMoon")? 256:64;
        //不在主世界也依然是64
        if(mob.getWorld().getRegistryKey()== RegistryKey.of(RegistryKeys.WORLD, Identifier.of(MOD_ID, "underworld")))
            range=range*0.75;
        return range;
    }


}
