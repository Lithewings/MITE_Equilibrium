package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.entity.Bucketable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_NO_ANIMALS;
import static net.minecraft.component.DataComponentTypes.ENCHANTMENTS;

@Mixin(FishEntity.class)
public abstract class FishEntityMixin extends WaterCreatureEntity implements Bucketable {

    @Shadow public abstract void setFromBucket(boolean fromBucket);

    protected FishEntityMixin(EntityType<? extends WaterCreatureEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
        if(world.getServer()!=null && getGameBooleanRuleFromServer(ENABLE_NO_ANIMALS, world.getServer()))
            return false;
        return super.canSpawn(world,spawnReason);
    }

}
