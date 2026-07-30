package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_UNIVERSAL_AGGRO;

@Mixin(ZombifiedPiglin.class)
public abstract class ZombifiedPiglinEntityMixin extends Zombie implements NeutralMob {


    @Shadow protected abstract void customServerAiStep();

    public ZombifiedPiglinEntityMixin(EntityType<? extends Zombie> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public boolean isAngryAt(LivingEntity entity) {
        boolean enableUniversalAggro = getGameBooleanRuleFromServer(ENABLE_UNIVERSAL_AGGRO,this.getServer());
        if(enableUniversalAggro){
            if (!this.canAttack(entity)) {
                return false;
            } else {
                return entity.getType() == EntityType.PLAYER ? true : entity.getUUID().equals(this.getPersistentAngerTarget());
            }
        }
        else
          return NeutralMob.super.isAngryAt(entity);


    }
}

