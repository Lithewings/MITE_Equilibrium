package com.equilibrium.entity.mob;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.equilibrium.OnServerInitialize.MOD_ID;
import static com.equilibrium.util.XpHashMap.getXpForLevel;


public class WoodenSpiderEntity extends SpiderEntity {
    public WoodenSpiderEntity(EntityType<? extends SpiderEntity> entityType, World world) {
        super(entityType, world);
    }
    @Override
    public void onAttacking(Entity target) {
        super.onAttacking(target);
        if (target instanceof PlayerEntity player) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 0));
        }
    }

    @Override
    protected int getXpToDrop() {
        return getXpForLevel(1);
    }

    @Override
    protected RegistryKey<LootTable> getLootTableId() {
        return RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(MOD_ID,"entity/wooden_spider"));
    }
    public static boolean checkSpawnPosition(World world, BlockPos pos) {
        for (int i = -3; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k =-3; k < 3; k++) {
                    BlockPos checkPos = pos.add(i,j,k);
                    BlockState state = world.getBlockState(checkPos);
                    if(state.isIn(BlockTags.LOGS)){
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        return entityData;
    }



}
