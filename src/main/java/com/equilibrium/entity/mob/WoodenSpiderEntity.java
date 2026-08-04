package com.equilibrium.entity.mob;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import static com.equilibrium.OnServerInitialize.MOD_ID;
import static com.equilibrium.util.XpHashMap.getXpForLevel;


public class WoodenSpiderEntity extends Spider {
    public WoodenSpiderEntity(EntityType<? extends Spider> entityType, Level world) {
        super(entityType, world);
    }
    @Override
    public void setLastHurtMob(Entity target) {
        super.setLastHurtMob(target);
        if (target instanceof Player player) {
            player.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
        }
    }

    @Override
    protected int getBaseExperienceReward() {
        return getXpForLevel(1);
    }

    @Override
    protected ResourceKey<LootTable> getDefaultLootTable() {
        return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(MOD_ID,"entity/wooden_spider"));
    }
    public static boolean checkSpawnPosition(Level world, BlockPos pos) {
        for (int i = -3; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k =-3; k < 3; k++) {
                    BlockPos checkPos = pos.offset(i,j,k);
                    BlockState state = world.getBlockState(checkPos);
                    if(state.is(BlockTags.LOGS)){
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData) {
        return entityData;
    }



}
