package com.equilibrium.structure;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

public record UnderWorldDimensionOptions(Holder<DimensionType> dimensionTypeEntry, ChunkGenerator chunkGenerator) {
    public static final Codec<LevelStem> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            DimensionType.CODEC.fieldOf("type").forGetter(LevelStem::type),
                            ChunkGenerator.CODEC.fieldOf("generator").forGetter(LevelStem::generator)
                    )
                    .apply(instance, instance.stable(LevelStem::new))
    );
    public static final ResourceKey<LevelStem> OVERWORLD = ResourceKey.create(Registries.LEVEL_STEM, ResourceLocation.fromNamespaceAndPath("minecraft","overworld"));
    public static final ResourceKey<LevelStem> NETHER = ResourceKey.create(Registries.LEVEL_STEM, ResourceLocation.fromNamespaceAndPath("minecraft","the_nether"));
    public static final ResourceKey<LevelStem> END = ResourceKey.create(Registries.LEVEL_STEM, ResourceLocation.fromNamespaceAndPath("minecraft","the_end"));
    public static final ResourceKey<LevelStem> UNDERWORLD = ResourceKey.create(Registries.LEVEL_STEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium","underworld"));
    //调用规则:如果是原版世界,用Identifier.of("minecraft","xxx"),若为自定义维度,则用Identifier.of("miteeqilibrium","xxx")

}

