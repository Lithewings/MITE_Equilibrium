package com.equilibrium.mixin.structure_and_dimension.village;

import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
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
import net.minecraft.world.gen.structure.DimensionPadding;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(StructurePoolBasedGenerator.class)
public class structurePoolBasedGeneratorMixin {

    @Shadow @Final private static Logger LOGGER;



    private static Optional<? extends RegistryKey<?>> key;




    private static boolean regEntryContains(@NotNull RegistryEntry<?> entry, String pattern) {
        key = entry.getKey();
        if (key != null && key.isPresent()) return key.get().getValue().getPath().contains(pattern);
        return false;
    }

    private static boolean getStructureGenerateValidity(MinecraftServer server){
        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(server);
        return serverState.isPickAxeCrafted;

    }


    @Inject(method = "generate(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/util/Identifier;ILnet/minecraft/util/math/BlockPos;Z)Z", at = @At("HEAD"), cancellable = true)
    private static void generate(ServerWorld world, RegistryEntry<StructurePool> structurePool, Identifier id, int size, BlockPos pos, boolean keepJigsaws, CallbackInfoReturnable<Boolean> cir) {
//        cir.cancel();

//        if(ServerInfoRecorder.getDay() <=8)
//            cir.cancel();
        //        MinecraftServer server = ServerInfoRecorder.getServerInstance();
//        if (server != null && ServerInfoRecorder.getDay() >= 16 && getStructureGenerateValidity(server)) {
//            //正常生成结构,包括村庄
//        } else {
//            //对村庄进行拦截
//            if (regEntryContains(structurePool, "village")) {
//                cir.setReturnValue(false);
//                cir.cancel();
//            }
//            //接着生成其他结构
//            cir.setReturnValue(false);
//            cir.cancel();
//        }

    }





//        if (ServerInfoRecorder.getDay() < 16 && server != null) {
//            //不生成村庄的逻辑
//            if (regEntryContains(structurePool, "village")) {
//                cir.setReturnValue(false);
//                cir.cancel();
//            } else if (server != null && getStructureGenerateValidity(server)) {
//                //合成金属镐检查
//                //不生成村庄的逻辑
//                if (regEntryContains(structurePool, "village")) {
//                    cir.setReturnValue(false);
//                    cir.cancel();
//                }
//            } else {
//                //什么都不做
//            }
//
//        }


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

        // 检查是否是村庄或掠夺者前哨站
        if (regEntryContains(structurePool, "village") || regEntryContains(structurePool, "pillager_outpost")) {

            // 只在服务器端世界进行检查
            if (context.world() instanceof ServerWorld serverWorld) {
                MinecraftServer server = serverWorld.getServer();
                int day = (int) (serverWorld.getTimeOfDay() / 24000L);


                // 如果条件不满足，取消生成
                if (!(day >= 10 && getStructureGenerateValidity(server))) {
                    cir.setReturnValue(Optional.empty());
                    cir.cancel();
                }
            }

            boolean far = Math.abs(pos.getX()) >= 1000 || Math.abs(pos.getZ()) >= 1000;
            if(!far){
                cir.setReturnValue(Optional.empty());
                cir.cancel();
            }





        }
    }


}



