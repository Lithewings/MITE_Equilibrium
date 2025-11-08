package com.equilibrium.structure_generator;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public record UnderWorldDimensionOptions(RegistryEntry<DimensionType> dimensionTypeEntry, ChunkGenerator chunkGenerator) {
    public static final Codec<net.minecraft.world.dimension.DimensionOptions> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            DimensionType.REGISTRY_CODEC.fieldOf("type").forGetter(net.minecraft.world.dimension.DimensionOptions::dimensionTypeEntry),
                            ChunkGenerator.CODEC.fieldOf("generator").forGetter(net.minecraft.world.dimension.DimensionOptions::chunkGenerator)
                    )
                    .apply(instance, instance.stable(net.minecraft.world.dimension.DimensionOptions::new))
    );
    public static final RegistryKey<DimensionOptions> OVERWORLD = RegistryKey.of(RegistryKeys.DIMENSION, Identifier.of("minecraft","overworld"));
    public static final RegistryKey<DimensionOptions> NETHER = RegistryKey.of(RegistryKeys.DIMENSION, Identifier.of("minecraft","the_nether"));
    public static final RegistryKey<DimensionOptions> END = RegistryKey.of(RegistryKeys.DIMENSION, Identifier.of("minecraft","the_end"));
    public static final RegistryKey<DimensionOptions> UNDERWORLD = RegistryKey.of(RegistryKeys.DIMENSION, Identifier.of("miteequilibrium","underworld"));
    //调用规则:如果是原版世界,用Identifier.of("minecraft","xxx"),若为自定义维度,则用Identifier.of("miteeqilibrium","xxx")

}

