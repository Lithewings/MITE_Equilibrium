package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_NO_ANIMALS;

@Mixin(AbstractFish.class)
public abstract class FishEntityMixin extends WaterAnimal implements Bucketable {

    @Shadow public abstract void setFromBucket(boolean fromBucket);

    protected FishEntityMixin(EntityType<? extends WaterAnimal> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor world, MobSpawnType spawnReason) {
        if(world.getServer()!=null && getGameBooleanRuleFromServer(ENABLE_NO_ANIMALS, world.getServer()))
            return false;
        return super.checkSpawnRules(world,spawnReason);
    }
}
