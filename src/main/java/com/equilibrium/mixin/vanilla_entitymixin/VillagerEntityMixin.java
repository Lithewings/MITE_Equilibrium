package com.equilibrium.mixin.vanilla_entitymixin;

import com.equilibrium.util.ServerInfoRecorder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity implements InteractionObserver, VillagerDataContainer {
    @Shadow private boolean natural;

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }


    @Inject(method = "mobTick",at = @At("HEAD"))
    protected void mobTick(CallbackInfo ci) {
//只有结构生成的村民才会有natural = true的标签
//        if (spawnReason == SpawnReason.STRUCTURE) {
//            this.natural = true;
//        }
        if(this.natural)
            this.setHealth(0);
    }

    @Inject(method = "canSummonGolem",at = @At("HEAD"),cancellable = true)
    public void canSummonGolem(long time, CallbackInfoReturnable<Boolean> cir) {

        if(ServerInfoRecorder.getDay()<=32)
            cir.setReturnValue(false);
    }
}
