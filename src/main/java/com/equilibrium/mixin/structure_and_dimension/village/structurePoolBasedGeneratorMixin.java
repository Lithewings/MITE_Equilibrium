package com.equilibrium.mixin.structure_and_dimension.village;

import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.alias.StructurePoolAliasLookup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.DimensionPadding;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.*;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_RESTRICT_VILLAGE_GEN;

@Mixin(StructurePoolBasedGenerator.class)
public abstract class structurePoolBasedGeneratorMixin {

    @Shadow
    @Final
    private static Logger LOGGER;


    private static Optional<? extends RegistryKey<?>> key;

    @Unique
    private static boolean regEntryContains(@NotNull RegistryEntry<?> entry, String pattern) {
        key = entry.getKey();
        if (key != null && key.isPresent()) return key.get().getValue().getPath().contains(pattern);
        return false;
    }


    @Inject(method = "generate(Lnet/minecraft/world/gen/structure/Structure$Context;Lnet/minecraft/registry/entry/RegistryEntry;Ljava/util/Optional;ILnet/minecraft/util/math/BlockPos;ZLjava/util/Optional;ILnet/minecraft/structure/pool/alias/StructurePoolAliasLookup;Lnet/minecraft/world/gen/structure/DimensionPadding;Lnet/minecraft/structure/StructureLiquidSettings;)Ljava/util/Optional;",
            at = @At("HEAD"),
            cancellable = true)
    private static void generate(
            Structure.Context context,
            RegistryEntry<StructurePool> structurePool,
            Optional<Identifier> id,
            int size,
            BlockPos pos,
            boolean useExpansionHack,
            Optional<Heightmap.Type> projectStartToHeightmap,
            int maxDistanceFromCenter,
            StructurePoolAliasLookup aliasLookup,
            DimensionPadding dimensionPadding,
            StructureLiquidSettings liquidSettings,
            CallbackInfoReturnable<Optional<Structure.StructurePosition>> cir) {

        // 在初始环境没有server上下文时,先行筛选一下
        if (regEntryContains(structurePool, "village") || regEntryContains(structurePool, "pillager_outpost")) {
            boolean far = Math.abs(pos.getX()) >= 1000 || Math.abs(pos.getZ()) >= 1000;
            if (!far) {
                cir.setReturnValue(Optional.empty());
                cir.cancel();
            }
        }

    }


}








