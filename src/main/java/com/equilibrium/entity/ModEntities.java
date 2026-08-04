package com.equilibrium.entity;

import com.equilibrium.entity.mob.*;
import com.equilibrium.entity.mob.earth_elemental.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import static com.equilibrium.OnServerInitialize.MOD_ID;

/**
 * 实体注册与属性注册的统一管理类。
 * 使用 @EventBusSubscriber 自动注册事件监听，无需在主类中手动添加。
 */
@EventBusSubscriber(modid = MOD_ID)
public class ModEntities {

    // ---------- 实体类型字段（声明，不赋值） ----------
    public static EntityType<InvisibleStalkerEntity> INVISIBLE_STALKER;
    public static EntityType<GhoulEntity> GHOUL;
    public static EntityType<ShadowEntity> SHADOW;
    public static EntityType<WightEntity> WIGHT;
    public static EntityType<LongDeadEntity> LONG_DEAD;
    public static EntityType<BoneLordEntity> BONE_LORD;
    public static EntityType<RevenantEntity> REVENANT;
    public static EntityType<PuddingSlimeEntity> PUDDING;
    public static EntityType<WoodenSpiderEntity> WOODEN_SPIDER;
    public static EntityType<StoneElementalEntity> STONE_ELEMENTAL;
    public static EntityType<ObsidianElementalEntity> OBSIDIAN_ELEMENTAL;
    public static EntityType<EndRockElementalEntity> END_ROCK_ELEMENTAL;
    public static EntityType<NetherrackElementalEntity> NETHERROCK_ELEMENTAL;
    public static EntityType<FireElementalEntity> FIRE_ELEMENTAL;

    // ---------- 实体注册（在 RegisterEvent 中执行） ----------
    @SubscribeEvent
    public static void registerEntities(RegisterEvent event) {
        // 只处理实体类型的注册
        if (!event.getRegistryKey().equals(BuiltInRegistries.ENTITY_TYPE.key())) {
            return;
        }

        // 普通怪物
        INVISIBLE_STALKER = Registry.register(BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "invisible_stalker"),
                EntityType.Builder.of(InvisibleStalkerEntity::new, MobCategory.MONSTER)
                        .sized(0.75f, 1.95f)
                        .build("invisible_stalker")
        );

        GHOUL = Registry.register(BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "ghoul"),
                EntityType.Builder.of(GhoulEntity::new, MobCategory.MONSTER)
                        .sized(0.75f, 1.95f)
                        .build("ghoul")
        );

        SHADOW = Registry.register(BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "shadow"),
                EntityType.Builder.of(ShadowEntity::new, MobCategory.MONSTER)
                        .sized(0.75f, 1.95f)
                        .build("shadow")
        );

        WIGHT = Registry.register(BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "wight"),
                EntityType.Builder.of(WightEntity::new, MobCategory.MONSTER)
                        .sized(0.75f, 1.95f)
                        .build("wight")
        );

        LONG_DEAD = Registry.register(BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "longdead"),
                EntityType.Builder.of(LongDeadEntity::new, MobCategory.MONSTER)
                        .sized(0.75f, 1.95f)
                        .build("longdead")
        );

        BONE_LORD = Registry.register(BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "bone_lord"),
                EntityType.Builder.of(BoneLordEntity::new, MobCategory.MONSTER)
                        .sized(0.75f, 1.95f)
                        .build("bone_lord")
        );

        REVENANT = Registry.register(BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "revenant"),
                EntityType.Builder.of(RevenantEntity::new, MobCategory.MONSTER)
                        .sized(0.75f, 1.95f)
                        .build("revenant")
        );

        PUDDING = Registry.register(BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "pudding"),
                EntityType.Builder.of(PuddingSlimeEntity::new, MobCategory.MONSTER)
                        .sized(0.75f, 1.95f)
                        .build("pudding")
        );

        WOODEN_SPIDER = Registry.register(BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "wooden_spider"),
                EntityType.Builder.of(WoodenSpiderEntity::new, MobCategory.MONSTER)
                        .sized(0.8F, 0.45F)
                        .build("wooden_spider")
        );

        // 元素实体（带方块参数的构造器）
        STONE_ELEMENTAL = Registry.register(BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "stone_elemental"),
                EntityType.Builder.of(
                                (EntityType<StoneElementalEntity> type, Level world) ->
                                        new StoneElementalEntity(type, world, Blocks.STONE),
                                MobCategory.MONSTER
                        )
                        .sized(0.75f, 1.95f)
                        .build("stone_elemental")
        );

        OBSIDIAN_ELEMENTAL = Registry.register(BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "obsidian_elemental"),
                EntityType.Builder.of(
                                (EntityType<ObsidianElementalEntity> type, Level world) ->
                                        new ObsidianElementalEntity(type, world, Blocks.OBSIDIAN),
                                MobCategory.MONSTER
                        )
                        .sized(0.75f, 1.95f)
                        .build("obsidian_elemental")
        );

        END_ROCK_ELEMENTAL = Registry.register(BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "end_rock_elemental"),
                EntityType.Builder.of(
                                (EntityType<EndRockElementalEntity> type, Level world) ->
                                        new EndRockElementalEntity(type, world, Blocks.END_STONE),
                                MobCategory.MONSTER
                        )
                        .sized(0.75f, 1.95f)
                        .build("end_rock_elemental")
        );

        NETHERROCK_ELEMENTAL = Registry.register(BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "netherrack_elemental"),
                EntityType.Builder.of(
                                (EntityType<NetherrackElementalEntity> type, Level world) ->
                                        new NetherrackElementalEntity(type, world, Blocks.NETHERRACK),
                                MobCategory.MONSTER
                        )
                        .sized(0.75f, 1.95f)
                        .build( "netherrack_elemental")
        );

        FIRE_ELEMENTAL = Registry.register(BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "fire_elemental"),
                EntityType.Builder.of(FireElementalEntity::new, MobCategory.MONSTER)
                        .immuneTo(Blocks.LAVA)
                        .sized(0.75f, 1.95f)
                        .build("fire_elemental")
        );
    }

    // ---------- 属性注册（在 EntityAttributeCreationEvent 中执行） ----------
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        // 普通怪物
        event.put(INVISIBLE_STALKER,
                Monster.createMonsterAttributes()
                        .add(Attributes.FOLLOW_RANGE, 35.0)
                        .add(Attributes.MOVEMENT_SPEED, 0.23F)
                        .add(Attributes.ATTACK_DAMAGE, 0.5)
                        .add(Attributes.ARMOR, 2.0)
                        .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
                        .add(Attributes.MAX_HEALTH, 20).build()
        );

        event.put(GHOUL,
                Monster.createMonsterAttributes()
                        .add(Attributes.FOLLOW_RANGE, 35.0)
                        .add(Attributes.MOVEMENT_SPEED, 0.23F)
                        .add(Attributes.ATTACK_DAMAGE, 3.0)
                        .add(Attributes.ARMOR, 2.0)
                        .add(Attributes.MAX_HEALTH, 20)
                        .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE).build()
        );

        event.put(SHADOW,
                Monster.createMonsterAttributes()
                        .add(Attributes.FOLLOW_RANGE, 35.0)
                        .add(Attributes.MOVEMENT_SPEED, 0.23F)
                        .add(Attributes.ATTACK_DAMAGE, 2.0)
                        .add(Attributes.ARMOR, 2.0)
                        .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
                        .add(Attributes.MAX_HEALTH, 20).build()
        );

        event.put(WIGHT,
                Monster.createMonsterAttributes()
                        .add(Attributes.FOLLOW_RANGE, 35.0)
                        .add(Attributes.MOVEMENT_SPEED, 0.23F)
                        .add(Attributes.ATTACK_DAMAGE, 2.0)
                        .add(Attributes.ARMOR, 2.0)
                        .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
                        .add(Attributes.MAX_HEALTH, 20).build()
        );

        event.put(LONG_DEAD,
                Monster.createMonsterAttributes()
                        .add(Attributes.MOVEMENT_SPEED, 0.25F)
                        .add(Attributes.ARMOR, 2.0)
                        .add(Attributes.ATTACK_DAMAGE, 0.5).build()
        );

        event.put(BONE_LORD,
                Monster.createMonsterAttributes()
                        .add(Attributes.MOVEMENT_SPEED, 0.25F)
                        .add(Attributes.ARMOR, 2.0)
                        .add(Attributes.FOLLOW_RANGE, 32)
                        .add(Attributes.ATTACK_DAMAGE, 0.5).build()
        );

        event.put(REVENANT,
                Monster.createMonsterAttributes()
                        .add(Attributes.FOLLOW_RANGE, 35.0)
                        .add(Attributes.MOVEMENT_SPEED, 0.23F)
                        .add(Attributes.ATTACK_DAMAGE, 1.0)
                        .add(Attributes.ARMOR, 0.0)
                        .add(Attributes.MAX_HEALTH, 30)
                        .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE).build()
        );

        event.put(WOODEN_SPIDER,
                Spider.createAttributes()
                        .add(Attributes.ATTACK_DAMAGE, 1.0)
                        .add(Attributes.MAX_HEALTH, 8).build()
        );

        event.put(PUDDING,
                Monster.createMonsterAttributes()
                        .add(Attributes.MOVEMENT_SPEED)
                        .add(Attributes.ATTACK_DAMAGE)
                        .add(Attributes.MAX_HEALTH).build()
        );

        // 元素实体（共用一套基础属性）
        var elementalBuilder = Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.18F)
                .add(Attributes.ATTACK_DAMAGE, 12)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.KNOCKBACK_RESISTANCE);

        event.put(STONE_ELEMENTAL, elementalBuilder.build());
        event.put(OBSIDIAN_ELEMENTAL, elementalBuilder.build());
        event.put(END_ROCK_ELEMENTAL, elementalBuilder.build());
        event.put(NETHERROCK_ELEMENTAL, elementalBuilder.build());

        // 火元素单独设置
        event.put(FIRE_ELEMENTAL,
                Monster.createMonsterAttributes()
                        .add(Attributes.FOLLOW_RANGE, 16.0)
                        .add(Attributes.MOVEMENT_SPEED, 0.23F)
                        .add(Attributes.ATTACK_DAMAGE, 1.0)
                        .add(Attributes.ARMOR, 0.0)
                        .add(Attributes.MAX_HEALTH, 20).build()
        );
    }
}