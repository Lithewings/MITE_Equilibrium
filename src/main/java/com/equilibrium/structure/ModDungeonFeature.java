package com.equilibrium.structure;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import org.slf4j.Logger;


import static com.equilibrium.OnServerInitialize.MOD_ID;
import static com.equilibrium.entity.ModEntities.LONG_DEAD;


public class ModDungeonFeature extends Feature<NoneFeatureConfiguration> {



    private static final Logger LOGGER = LogUtils.getLogger();

    private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();

    public ModDungeonFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        Predicate<BlockState> predicate = Feature.isReplaceable(BlockTags.FEATURES_CANNOT_REPLACE);
        BlockPos blockPos = context.origin();
        RandomSource random = context.random();
        WorldGenLevel structureWorldAccess = context.level();
        int i = 3;
        int j = random.nextInt(2) + 2;
        int k = -j - 1;
        int l = j + 1;
        int m = -1;
        int n = 4;
        int o = random.nextInt(2) + 2;
        int p = -o - 1;
        int q = o + 1;
        int r = 0;

        for (int s = k; s <= l; s++) {
            for (int t = -1; t <= 4; t++) {
                for (int u = p; u <= q; u++) {
                    BlockPos blockPos2 = blockPos.offset(s, t, u);
                    boolean bl = structureWorldAccess.getBlockState(blockPos2).isSolid();
                    if (t == -1 && !bl) {
                        return false;
                    }

                    if (t == 4 && !bl) {
                        return false;
                    }

                    if ((s == k || s == l || u == p || u == q) && t == 0 && structureWorldAccess.isEmptyBlock(blockPos2) && structureWorldAccess.isEmptyBlock(blockPos2.above())) {
                        r++;
                    }
                }
            }
        }

        if (r >= 1 && r <= 5) {
            for (int s = k; s <= l; s++) {
                for (int t = 3; t >= -1; t--) {
                    for (int u = p; u <= q; u++) {
                        BlockPos blockPos2x = blockPos.offset(s, t, u);
                        BlockState blockState = structureWorldAccess.getBlockState(blockPos2x);
                        if (s == k || t == -1 || u == p || s == l || t == 4 || u == q) {
                            if (blockPos2x.getY() >= structureWorldAccess.getMinBuildHeight() && !structureWorldAccess.getBlockState(blockPos2x.below()).isSolid()) {
                                structureWorldAccess.setBlock(blockPos2x, AIR, Block.UPDATE_CLIENTS);
                            } else if (blockState.isSolid() && !blockState.is(Blocks.CHEST)) {
                                if (t == -1 && random.nextInt(4) != 0) {
                                    this.safeSetBlock(structureWorldAccess, blockPos2x, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), predicate);
                                } else {
                                    this.safeSetBlock(structureWorldAccess, blockPos2x, Blocks.COBBLESTONE.defaultBlockState(), predicate);
                                }
                            }
                        } else if (!blockState.is(Blocks.CHEST) && !blockState.is(Blocks.SPAWNER)) {
                            this.safeSetBlock(structureWorldAccess, blockPos2x, AIR, predicate);
                        }
                    }
                }
            }

            for (int s = 0; s < 2; s++) {
                for (int t = 0; t < 3; t++) {
                    int ux = blockPos.getX() + random.nextInt(j * 2 + 1) - j;
                    int v = blockPos.getY();
                    int w = blockPos.getZ() + random.nextInt(o * 2 + 1) - o;
                    BlockPos blockPos3 = new BlockPos(ux, v, w);
                    if (structureWorldAccess.isEmptyBlock(blockPos3)) {
                        int x = 0;

                        for (Direction direction : Direction.Plane.HORIZONTAL) {
                            if (structureWorldAccess.getBlockState(blockPos3.relative(direction)).isSolid()) {
                                x++;
                            }
                        }

                        if (x == 1) {
                            this.safeSetBlock(
                                    structureWorldAccess, blockPos3, StructurePiece.reorient(structureWorldAccess, blockPos3, Blocks.CHEST.defaultBlockState()), predicate
                            );
                            RandomizableContainer.setBlockEntityLootTable(structureWorldAccess, random, blockPos3,ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(MOD_ID,"chests/simple_dungeon")));
                            break;
                        }
                    }
                }
            }

            this.safeSetBlock(structureWorldAccess, blockPos, Blocks.SPAWNER.defaultBlockState(), predicate);
            if (structureWorldAccess.getBlockEntity(blockPos) instanceof SpawnerBlockEntity mobSpawnerBlockEntity) {
                mobSpawnerBlockEntity.setEntityId(LONG_DEAD, random);
            } else {
                LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", blockPos.getX(), blockPos.getY(), blockPos.getZ());
            }

            return true;
        } else {
            return false;
        }
    }
}
