package com.equilibrium.mixin.structure_and_dimension.structure;

import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.function.Consumer;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.DISABLE_VILLAGE_AND_PILLAGE;
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
            method = "createStructures",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"
            )
    )
    private void filterStructureSets(
            // 下面这 4 个参数会自动从方法里捕获（Mixin 高级特性）
            List<Holder<StructureSet>> instance,
            Consumer<Holder<StructureSet>> consumer,

            // 自动注入 setStructureStarts 的所有参数
            RegistryAccess registryManager,
            ChunkGeneratorStructureState placementCalculator,
            StructureManager structureAccessor,
            ChunkAccess chunk,
            StructureTemplateManager structureTemplateManager
    ) {
        boolean shouldGenVillage = false; // 禁用村庄
        if (structureAccessor.level instanceof ServerLevel serverWorld) {
            //禁用村庄的条件如下
            shouldGenVillage =
                    //游戏规则中,未使用生成限制规则,则这一整项均为true,则生成村庄
                    !getGameBooleanRuleFromServer(ENABLE_RESTRICT_VILLAGE_GEN, serverWorld.getServer())
                            //否则看这里:
                            //游戏时长大于等于10天,且制作出金属镐
                            ||
                            (serverWorld.getDayTime() / 24000L >= 10 && getStructureGenerateValidity(serverWorld.getServer()));

            //Extra:最后一步检查
            if(getGameBooleanRuleFromServer(DISABLE_VILLAGE_AND_PILLAGE,serverWorld.getServer()))
                shouldGenVillage=false;
        }
        // 遍历并跳过村庄或前哨站
        for (Holder<StructureSet> entry : instance) {
            ResourceLocation id = entry.unwrapKey().get().location();

            // 如果不应该生成村庄,在检索到村庄和前哨站时,跳过所有村庄或前哨站
            if (!shouldGenVillage && (id.toString().contains("village")||id.toString().contains("pillager_outpost"))) {
                continue;
            }

            // 正常生成其他结构
            consumer.accept(entry);
        }
    }
}
