package com.equilibrium.entity.mob.earth_elemental;

import org.jetbrains.annotations.Nullable;

import static com.equilibrium.OnServerInitialize.MOD_ID;
import static com.equilibrium.util.XpHashMap.getXpForLevel;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

public class EndRockElementalEntity extends AbstractEarthElementalEntity {

    public EndRockElementalEntity(EntityType<? extends Monster> entityType, Level world, Block material) {
        super(entityType, world, material);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData) {
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(this.getKnockBackResistance());
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
    }
    @Override
    public int getBaseExperienceReward() {
        return getXpForLevel(2);
    }

    @Override
    public ResourceKey<LootTable> getDefaultLootTable() {
        return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(MOD_ID, "entity/end_rock_elemental"));
    }
}
