package com.equilibrium.mixin.structure_and_dimension.village;

import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.BoundedRegionArray;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.GameRules;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_RESTRICT_VILLAGE_GEN;



@Mixin(ChunkGenerator.class)
public abstract class VillageGenerationControlMixin {
    @Unique
    private static boolean getStructureGenerateValidity(MinecraftServer server) {
        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(server);
        return serverState.isPickAxeCrafted;

    }

    // 在Server上下文中拦截村庄逻辑
    @Redirect(
            method = "setStructureStarts",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"
            )
    )
    private void filterStructureSets(
            // 下面这 4 个参数会自动从方法里捕获（Mixin 高级特性）
            List<RegistryEntry<StructureSet>> instance,
            Consumer<RegistryEntry<StructureSet>> consumer,

            // 自动注入 setStructureStarts 的所有参数
            DynamicRegistryManager registryManager,
            StructurePlacementCalculator placementCalculator,
            StructureAccessor structureAccessor,
            Chunk chunk,
            StructureTemplateManager structureTemplateManager
    ) {


        // ==============================================
        // 你的判断逻辑（游戏规则 / 世界 / 条件）
        // ==============================================
        boolean shouldGenVillage = false; // 禁用村庄
        // 你也可以用游戏规则：
        if (structureAccessor.world instanceof ServerWorld serverWorld)
            //禁用村庄的条件如下
            shouldGenVillage =
                    //游戏规则中,未使用生成限制规则,则这一整项均为true,则生成村庄
                    !getGameBooleanRuleFromServer(ENABLE_RESTRICT_VILLAGE_GEN,serverWorld.getServer())
                    //否则看这里:
                    //游戏时长大于等于10天,且制作出金属镐
                    ||
                    (serverWorld.getTimeOfDay()/24000L>=10 && getStructureGenerateValidity(serverWorld.getServer()));


        // 遍历并跳过村庄或前哨站
        for (RegistryEntry<StructureSet> entry : instance) {
            Identifier id = entry.getKey().get().getValue();

            // 如果不应该生成村庄,在检索到村庄和前哨站时,跳过所有村庄或前哨站
            if (!shouldGenVillage && (id.toString().contains("village")||id.toString().contains("pillager_outpost"))) {
                continue;
            }

            // 正常生成其他结构
            consumer.accept(entry);
        }
    }
}
