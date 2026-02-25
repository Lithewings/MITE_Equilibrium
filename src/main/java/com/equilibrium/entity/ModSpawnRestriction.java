package com.equilibrium.entity;




import com.equilibrium.entity.mob.FireElementalEntity;
import com.equilibrium.entity.mob.PuddingSlimeEntity;
import com.llamalad7.mixinextras.lib.apache.commons.ObjectUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnLocation;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.equilibrium.entity.ModEntities.*;


public class ModSpawnRestriction {


        static SpawnLocation ON_LAVA = new SpawnLocation() {
                @Override
                public boolean isSpawnPositionOk(WorldView worldView, BlockPos blockPos, @Nullable EntityType<?> entityType) {
                        if (entityType != null && worldView.getWorldBorder().contains(blockPos)) {
                                //blockPos1,这是第一个岩浆方块
                                BlockPos blockPos1= adjustPosition(worldView,blockPos);
                                return blockPos1==null?false:true;
                        } else {
                                return false;
                        }
                }
                @Override
                public BlockPos adjustPosition(WorldView world, BlockPos pos) {
                        while (world.getBlockState(pos).isAir() && pos.getY() > -64) {
                                pos = pos.down();
                                if (world.getBlockState(pos).isOf(Blocks.LAVA)) {
                                        return pos;
                                }
                        }
                        return null;
                }
        };

        //生成限制
        public static void registerModSpawnRestriction(){
                SpawnRestriction.register(LONG_DEAD, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
                SpawnRestriction.register(WIGHT, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
                SpawnRestriction.register(GHOUL, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
                SpawnRestriction.register(SHADOW, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
                SpawnRestriction.register(INVISIBLE_STALKER, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
                SpawnRestriction.register(PUDDING, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, PuddingSlimeEntity::canPuddingSpawn);

                SpawnRestriction.register(BONE_LORD, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);

                SpawnRestriction.register(STONE_ELEMENTAL, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
                SpawnRestriction.register(OBSIDIAN_ELEMENTAL, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
                SpawnRestriction.register(END_ROCK_ELEMENTAL, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
                SpawnRestriction.register(NETHERROCK_ELEMENTAL, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);

                SpawnRestriction.register(REVENANT, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
                SpawnRestriction.register(
                        FIRE_ELEMENTAL,
                        ON_LAVA,
                        Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                        FireElementalEntity::canSpawn
                );







        }
        }

