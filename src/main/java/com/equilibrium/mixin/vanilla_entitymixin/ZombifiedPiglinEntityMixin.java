package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_UNIVERSAL_AGGRO;

@Mixin(ZombifiedPiglinEntity.class)
public abstract class ZombifiedPiglinEntityMixin extends ZombieEntity implements Angerable {


    @Shadow protected abstract void mobTick();

    public ZombifiedPiglinEntityMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean shouldAngerAt(LivingEntity entity) {
        boolean enableUniversalAggro = getGameBooleanRuleFromServer(ENABLE_UNIVERSAL_AGGRO,this.getServer());
        if(enableUniversalAggro){
            if (!this.canTarget(entity)) {
                return false;
            } else {
                return entity.getType() == EntityType.PLAYER ? true : entity.getUuid().equals(this.getAngryAt());
            }
        }
        else
          return Angerable.super.shouldAngerAt(entity);


    }
}

