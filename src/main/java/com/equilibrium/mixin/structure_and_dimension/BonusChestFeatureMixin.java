package com.equilibrium.mixin.structure_and_dimension;

import com.equilibrium.util.RenderBeaconBeam;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.BonusChestFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

import java.util.stream.IntStream;

@Mixin(BonusChestFeature.class)
//让奖励箱上生成一束信标光柱
public class BonusChestFeatureMixin extends Feature<NoneFeatureConfiguration> {
    public BonusChestFeatureMixin(Codec<NoneFeatureConfiguration> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource random = context.random();
        WorldGenLevel structureWorldAccess = context.level();
        ChunkPos chunkPos = new ChunkPos(context.origin());
        IntArrayList intArrayList = Util.toShuffledList(IntStream.rangeClosed(chunkPos.getMinBlockX(), chunkPos.getMaxBlockX()), random);
        IntArrayList intArrayList2 = Util.toShuffledList(IntStream.rangeClosed(chunkPos.getMinBlockZ(), chunkPos.getMaxBlockZ()), random);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();



        for (Integer integer : intArrayList) {
            for (Integer integer2 : intArrayList2) {
                mutable.set(integer, 0, integer2);
                BlockPos blockPos = structureWorldAccess.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mutable);
                if (structureWorldAccess.isEmptyBlock(blockPos) || structureWorldAccess.getBlockState(blockPos).getCollisionShape(structureWorldAccess, blockPos).isEmpty()) {
                    structureWorldAccess.setBlock(blockPos, Blocks.CHEST.defaultBlockState(), Block.UPDATE_CLIENTS);
                    BlockPos chestPos = blockPos;

                    RandomizableContainer.setBlockEntityLootTable(structureWorldAccess, random, blockPos, BuiltInLootTables.SPAWN_BONUS_CHEST);
                    BlockState blockState = Blocks.TORCH.defaultBlockState();

                    for (Direction direction : Direction.Plane.HORIZONTAL) {
                        BlockPos blockPos2 = blockPos.relative(direction);
                        if (blockState.canSurvive(structureWorldAccess, blockPos2)) {
                            structureWorldAccess.setBlock(blockPos2, blockState, Block.UPDATE_CLIENTS);
                        }
                    }
                    RenderBeaconBeam.show(Vec3.atLowerCornerOf(chestPos).add(-0.5,0,-0.5), context.level().getLevelData().getDayTime());
                    return true;
                }
            }
        }

        return false;
    }
}
