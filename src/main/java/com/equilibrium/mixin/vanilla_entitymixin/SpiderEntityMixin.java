package com.equilibrium.mixin.vanilla_entitymixin;

import com.equilibrium.entity.mob.WoodenSpiderEntity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.equilibrium.entity.ModEntities.WOODEN_SPIDER;
import static com.equilibrium.entity.mob.WoodenSpiderEntity.checkSpawnPosition;

@Mixin(SpiderEntity.class)
public class SpiderEntityMixin extends HostileEntity {
    protected SpiderEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initialize", at = @At("HEAD"))
    public void initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, CallbackInfoReturnable<EntityData> cir) {
        //Solve Stack Overflow Error
        if((Object)this instanceof WoodenSpiderEntity) {
            return;
        }
        if (!this.getWorld().isClient) {
            boolean isLogsExisting = checkSpawnPosition(this.getWorld(), this.getBlockPos());
            boolean shouldRespawn = this.getRandom().nextInt(8)==0;
            if (isLogsExisting && shouldRespawn) {
                WOODEN_SPIDER.spawn((ServerWorld)this.getWorld(),this.getBlockPos(),SpawnReason.NATURAL);
                this.discard();
            }
        }
    }
}
