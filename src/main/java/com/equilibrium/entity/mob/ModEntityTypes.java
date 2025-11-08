package com.equilibrium.entity.mob;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.equilibrium.MITEequilibrium.MOD_ID;

public class ModEntityTypes {
    public static final EntityType<LongDeadEntity> LONG_DEAD_ENTITY_ENTITY_TYPE = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "longdead"),
            //古尸的尺寸?再次定义了一次?
            EntityType.Builder.create(LongDeadEntity::new, SpawnGroup.MONSTER).dimensions(0.6F, 1.99F).build()
    );
    public static void modEntityTypeRegister(){}
}
