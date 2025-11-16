package com.equilibrium.entity.mob;

import com.google.common.collect.Maps;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnLocation;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.world.Heightmap;

import java.util.Map;

import static com.equilibrium.entity.mob.ModEntityTypes.LONG_DEAD_ENTITY_ENTITY_TYPE;

public class ModSpawnRestriction {
    private static final Map<EntityType<?>, Entry> RESTRICTIONS = Maps.<EntityType<?>, Entry>newHashMap();
    public static <T extends MobEntity> void register(
            EntityType<T> type, SpawnLocation location, Heightmap.Type heightmapType, SpawnRestriction.SpawnPredicate<T> predicate
    ) {
        Entry entry = (Entry)RESTRICTIONS.put(type, new Entry(heightmapType, location, predicate));
        if (entry != null) {
            throw new IllegalStateException("Duplicate registration for type " + Registries.ENTITY_TYPE.getId(type));
        }
    }
    static record Entry(Heightmap.Type heightmapType, SpawnLocation location, SpawnRestriction.SpawnPredicate<?> predicate) {
    }
    public static void setModSpawnRestriction(){
        register(LONG_DEAD_ENTITY_ENTITY_TYPE, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);

    }





}
