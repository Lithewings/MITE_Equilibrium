package com.equilibrium.entity.mob.earth_elemental;

import com.equilibrium.entity.mob.earth_elemental.AbstractEarthElementalEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import static com.equilibrium.OnServerInitialize.MOD_ID;
import static com.equilibrium.util.XpHashMap.getXpForLevel;

public class StoneElementalEntity extends AbstractEarthElementalEntity {

    //继承父类的字段,随构造函数进行赋值:protected final Block material;


    public StoneElementalEntity(EntityType<? extends AbstractEarthElementalEntity> entityType, Level world, Block material) {
        super(entityType, world, material);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData) {
        //已保证GENERIC_KNOCKBACK_RESISTANCE一定存在:
//        FabricDefaultAttributeRegistry.register(STONE_ELEMENTAL, HostileEntity.createHostileAttributes()
//                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0)
//                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.18F)
//                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.5)
//                .add(EntityAttributes.GENERIC_ARMOR, 8.0)
//                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
//                .add(EntityAttributes.GENERIC_MAX_HEALTH,20)
//                //具体值在不同的类中进行动态定义
//                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)
//        );

        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(this.getKnockBackResistance());


        return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
    }

    @Override
    public int getBaseExperienceReward() {
        return getXpForLevel(2);
    }

    @Override
    public ResourceKey<LootTable> getDefaultLootTable() {
        return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(MOD_ID, "entity/stone_elemental"));
    }

}
