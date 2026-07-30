package com.equilibrium.mixin.vanilla_entitymixin;

import com.equilibrium.entity.mob.WoodenSpiderEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.equilibrium.entity.ModEntities.WOODEN_SPIDER;
import static com.equilibrium.entity.mob.WoodenSpiderEntity.checkSpawnPosition;

@Mixin(Spider.class)
public class SpiderEntityMixin extends Monster {
    protected SpiderEntityMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    public void initialize(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, SpawnGroupData entityData, CallbackInfoReturnable<SpawnGroupData> cir) {
        //Solve Stack Overflow Error
        if((Object)this instanceof WoodenSpiderEntity) {
            return;
        }
        if (!this.level().isClientSide) {
            boolean isLogsExisting = checkSpawnPosition(this.level(), this.blockPosition());
            boolean shouldRespawn = this.getRandom().nextInt(8)==0;
            if (isLogsExisting && shouldRespawn) {
                WOODEN_SPIDER.spawn((ServerLevel)this.level(),this.blockPosition(),MobSpawnType.NATURAL);
                this.discard();
            }
        }
    }
}
