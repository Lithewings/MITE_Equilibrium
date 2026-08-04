package com.equilibrium.entity;

import com.equilibrium.entity.mob.FireElementalEntity;
import com.equilibrium.entity.mob.PuddingSlimeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import org.jetbrains.annotations.Nullable;

import static com.equilibrium.entity.ModEntities.*;

@EventBusSubscriber(modid = "miteequilibrium") // 替换为你的 modid
public class ModSpawnRestriction {

        private static final SpawnPlacementType ON_LAVA = new SpawnPlacementType() {
                @Override
                public boolean isSpawnPositionOk(LevelReader worldView, BlockPos blockPos, @Nullable EntityType<?> entityType) {
                        if (entityType != null && worldView.getWorldBorder().isWithinBounds(blockPos)) {
                                BlockPos blockPos1 = adjustSpawnPosition(worldView, blockPos);
                                return blockPos1 != null;
                        }
                        return false;
                }

                @Override
                public BlockPos adjustSpawnPosition(LevelReader world, BlockPos pos) {
                        while (world.getBlockState(pos).isAir() && pos.getY() > -64) {
                                pos = pos.below();
                                if (world.getBlockState(pos).is(Blocks.LAVA)) {
                                        return pos;
                                }
                        }
                        return null;
                }
        };

        @SubscribeEvent
        public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(LONG_DEAD, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,RegisterSpawnPlacementsEvent.Operation.OR );
                event.register(WIGHT, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,RegisterSpawnPlacementsEvent.Operation.OR );
                event.register(GHOUL, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,RegisterSpawnPlacementsEvent.Operation.OR );
                event.register(SHADOW, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,RegisterSpawnPlacementsEvent.Operation.OR );
                event.register(INVISIBLE_STALKER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,RegisterSpawnPlacementsEvent.Operation.OR );
                event.register(PUDDING, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, PuddingSlimeEntity::canPuddingSpawn,RegisterSpawnPlacementsEvent.Operation.OR );
                event.register(BONE_LORD, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,RegisterSpawnPlacementsEvent.Operation.OR );
                event.register(STONE_ELEMENTAL, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,RegisterSpawnPlacementsEvent.Operation.OR );
                event.register(OBSIDIAN_ELEMENTAL, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,RegisterSpawnPlacementsEvent.Operation.OR );
                event.register(END_ROCK_ELEMENTAL, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,RegisterSpawnPlacementsEvent.Operation.OR );
                event.register(NETHERROCK_ELEMENTAL, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,RegisterSpawnPlacementsEvent.Operation.OR );
                event.register(REVENANT, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,RegisterSpawnPlacementsEvent.Operation.OR );
                event.register(FIRE_ELEMENTAL, ON_LAVA, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FireElementalEntity::canSpawn,RegisterSpawnPlacementsEvent.Operation.OR );
        }
}