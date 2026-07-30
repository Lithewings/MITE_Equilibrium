package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerEntityMixin extends AbstractVillager implements ReputationEventHandler, VillagerDataHolder {
    @Shadow private boolean assignProfessionWhenSpawned;

    public VillagerEntityMixin(EntityType<? extends AbstractVillager> entityType, Level world) {
        super(entityType, world);
    }


    @Inject(method = "customServerAiStep",at = @At("HEAD"))
    protected void mobTick(CallbackInfo ci) {
    //只有结构生成的村民才会有natural = true的标签
        if(this.assignProfessionWhenSpawned)
            this.setHealth(0);
    }

}
