package com.equilibrium.entity;

import com.equilibrium.entity.mob.*;
import com.equilibrium.entity.mob.earth_elemental.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

import static com.equilibrium.OnServerInitialize.MOD_ID;


public class ModEntities {
    //注册实体,记得在客户端那边渲染


    public static final EntityType<InvisibleStalkerEntity> INVISIBLE_STALKER = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "invisible_stalker"),
            EntityType.Builder.create(InvisibleStalkerEntity::new, SpawnGroup.MONSTER).dimensions(0.75f, 1.95f).build());


    public static final EntityType<GhoulEntity> GHOUL = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "ghoul"),
            EntityType.Builder.create(GhoulEntity::new, SpawnGroup.MONSTER).dimensions(0.75f, 1.95f).build());


    public static final EntityType<ShadowEntity> SHADOW = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "shadow"),
            EntityType.Builder.create(ShadowEntity::new, SpawnGroup.MONSTER).dimensions(0.75f, 1.95f).build());


    public static final EntityType<WightEntity> WIGHT = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "wight"),
            EntityType.Builder.create(WightEntity::new, SpawnGroup.MONSTER).dimensions(0.75f, 1.95f).build());

    public static final EntityType<LongDeadEntity> LONG_DEAD = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "longdead"),
            EntityType.Builder.create(LongDeadEntity::new, SpawnGroup.MONSTER).dimensions(0.75f, 1.95f).build());

    public static final EntityType<BoneLordEntity> BONE_LORD = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "bone_lord"),
            EntityType.Builder.create(BoneLordEntity::new, SpawnGroup.MONSTER).dimensions(0.75f, 1.95f).build());


    public static final EntityType<PuddingSlimeEntity> PUDDING = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "pudding"),
            EntityType.Builder.create(PuddingSlimeEntity::new, SpawnGroup.MONSTER).dimensions(0.75f, 1.95f).build());


    public static final EntityType<RevenantEntity> REVENANT = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "revenant"),
            EntityType.Builder.create(RevenantEntity::new, SpawnGroup.MONSTER).dimensions(0.75f, 1.95f).build());

    public static final EntityType<FireElementalEntity> FIRE_ELEMENTAL = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "fire_elemental"),
            EntityType.Builder.create(FireElementalEntity::new, SpawnGroup.MONSTER).allowSpawningInside(Blocks.LAVA).dimensions(0.75f, 1.95f).build());


    public static final EntityType<WoodenSpiderEntity> WOODEN_SPIDER = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "wooden_spider"),
            EntityType.Builder.create(WoodenSpiderEntity::new, SpawnGroup.MONSTER).dimensions(0.8F, 0.45F).build());

    public static final EntityType<StoneElementalEntity> STONE_ELEMENTAL = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "stone_elemental"),
            EntityType.Builder.create(
                    (EntityType<StoneElementalEntity> type, World world) ->
                            new StoneElementalEntity(type, world, Blocks.STONE), // 传入具体方块
                    SpawnGroup.MONSTER
            ).dimensions(0.75f, 1.95f).build());


    public static final EntityType<ObsidianElementalEntity> OBSIDIAN_ELEMENTAL = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "obsidian_elemental"),
            EntityType.Builder.create(
                    (EntityType<ObsidianElementalEntity> type, World world) ->
                            new ObsidianElementalEntity(type, world, Blocks.OBSIDIAN), // 传入具体方块
                    SpawnGroup.MONSTER
            ).dimensions(0.75f, 1.95f).build());

    public static final EntityType<EndRockElementalEntity> END_ROCK_ELEMENTAL = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "end_rock_elemental"),
            EntityType.Builder.create(
                    (EntityType<EndRockElementalEntity> type, World world) ->
                            new EndRockElementalEntity(type, world, Blocks.END_STONE), // 传入具体方块
                    SpawnGroup.MONSTER
            ).dimensions(0.75f, 1.95f).build());

    public static final EntityType<NetherrackElementalEntity> NETHERROCK_ELEMENTAL = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "netherrack_elemental"),
            EntityType.Builder.create(
                    (EntityType<NetherrackElementalEntity> type, World world) ->
                            new NetherrackElementalEntity(type, world, Blocks.NETHERRACK), // 传入具体方块
                    SpawnGroup.MONSTER
            ).dimensions(0.75f, 1.95f).build());


    private static void elementalAttributes(List<EntityType<? extends AbstractEarthElementalEntity>> list) {
        for (EntityType<? extends AbstractEarthElementalEntity> entityType : list) {
            FabricDefaultAttributeRegistry.register(entityType, HostileEntity.createHostileAttributes()
                    .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0)
                    .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.18F)
                    .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 12)
                    .add(EntityAttributes.GENERIC_ARMOR, 4.0)
                    .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
                    .add(EntityAttributes.GENERIC_MAX_HEALTH, 30)
                    //具体值在不同的类中进行动态定义
                    .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)
            );
        }
    }


    //注册属性
    public static void registerModEntities() {


        elementalAttributes(List.of(END_ROCK_ELEMENTAL, NETHERROCK_ELEMENTAL,OBSIDIAN_ELEMENTAL,STONE_ELEMENTAL));


        FabricDefaultAttributeRegistry.register(INVISIBLE_STALKER, HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.5)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0)
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20));

        FabricDefaultAttributeRegistry.register(GHOUL, HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS));


        FabricDefaultAttributeRegistry.register(SHADOW, HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0)
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20));

        FabricDefaultAttributeRegistry.register(WIGHT, HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0)
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20));

        FabricDefaultAttributeRegistry.register(LONG_DEAD, HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25F)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.5));

        FabricDefaultAttributeRegistry.register(BONE_LORD, HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25F)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.5));


        FabricDefaultAttributeRegistry.register(REVENANT, HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0)
                .add(EntityAttributes.GENERIC_ARMOR, 0.0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30)
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS));

        FabricDefaultAttributeRegistry.register(FIRE_ELEMENTAL, HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0)
                .add(EntityAttributes.GENERIC_ARMOR, 0.0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20));


        FabricDefaultAttributeRegistry.register(WOODEN_SPIDER, SpiderEntity.createSpiderAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 8));


        //setSize中会引用这三个属性
        FabricDefaultAttributeRegistry.register(PUDDING, HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE)
                .add(EntityAttributes.GENERIC_MAX_HEALTH));


    }


}


