package com.equilibrium.entity.mob.earth_elemental;

import com.equilibrium.entity.mob.earth_elemental.AbstractEarthElementalEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
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

public class StoneElementalEntity extends AbstractEarthElementalEntity {

    //继承父类的字段,随构造函数进行赋值:protected final Block material;


    public StoneElementalEntity(EntityType<? extends AbstractEarthElementalEntity> entityType, World world, Block material) {
        super(entityType, world, material);
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
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

        this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(this.getKnockBackResistance());


        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Override
    public int getXpToDrop() {
        return getXpForLevel(2);
    }

    @Override
    public RegistryKey<LootTable> getLootTableId() {
        return RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(MOD_ID, "entity/stone_elemental"));
    }

}
