package com.equilibrium.entity.mob.earth_elemental;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.equilibrium.OnServerInitialize.MOD_ID;
import static com.equilibrium.util.XpHashMap.getXpForLevel;

public class NetherrackElementalEntity extends AbstractEarthElementalEntity {

    public NetherrackElementalEntity(EntityType<? extends HostileEntity> entityType, World world, Block material) {
        super(entityType, world, material);
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(this.getKnockBackResistance());
        return super.initialize(world, difficulty, spawnReason, entityData);
    }
    @Override
    public int getXpToDrop() {
        return getXpForLevel(1);
    }

    @Override
    public RegistryKey<LootTable> getLootTableId() {
        return RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(MOD_ID, "entity/netherrack_elemental"));
    }
}
