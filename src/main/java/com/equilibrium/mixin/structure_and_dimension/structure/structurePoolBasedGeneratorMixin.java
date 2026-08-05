package com.equilibrium.mixin.structure_and_dimension.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
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

@Mixin(JigsawPlacement.class)
public abstract class structurePoolBasedGeneratorMixin {

    @Shadow
    @Final
    private static Logger LOGGER;


    private static Optional<? extends ResourceKey<?>> key;

    @Unique
    private static boolean regEntryContains(@NotNull Holder<?> entry, String pattern) {
        key = entry.unwrapKey();
        if (key != null && key.isPresent()) return key.get().location().getPath().contains(pattern);
        return false;
    }


    @Inject(method = "addPieces(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;Lnet/minecraft/core/Holder;Ljava/util/Optional;ILnet/minecraft/core/BlockPos;ZLjava/util/Optional;ILnet/minecraft/world/level/levelgen/structure/pools/alias/PoolAliasLookup;Lnet/minecraft/world/level/levelgen/structure/pools/DimensionPadding;Lnet/minecraft/world/level/levelgen/structure/templatesystem/LiquidSettings;)Ljava/util/Optional;",
            at = @At("HEAD"),
            cancellable = true)
    private static void generate(
            Structure.GenerationContext context,
            Holder<StructureTemplatePool> structurePool,
            Optional<ResourceLocation> id,
            int size,
            BlockPos pos,
            boolean useExpansionHack,
            Optional<Heightmap.Types> projectStartToHeightmap,
            int maxDistanceFromCenter,
            PoolAliasLookup aliasLookup,
            DimensionPadding dimensionPadding,
            LiquidSettings liquidSettings,
            CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {


        boolean far = Math.abs(pos.getX()) >= 1000 || Math.abs(pos.getZ()) >= 1000;
        if (!far) {
            cir.setReturnValue(Optional.empty());
            cir.cancel();
        }
//        // 在初始环境没有server上下文时,先行筛选一下
//        if (regEntryContains(structurePool, "village") || regEntryContains(structurePool, "pillager_outpost")) {
//            boolean far = Math.abs(pos.getX()) >= 1000 || Math.abs(pos.getZ()) >= 1000;
//            if (!far) {
//                cir.setReturnValue(Optional.empty());
//                cir.cancel();
//            }
//        }


    }


}








