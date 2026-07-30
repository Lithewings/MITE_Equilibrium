package com.equilibrium.util;

import com.equilibrium.OnServerInitialize;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class AdvancementRemover {
    private static final Logger LOGGER = OnServerInitialize.LOGGER;
    /**
     * 移除所有minecraft命名空间的成就
     * @param advancementManager 成就管理器
     */
    public static void removeAllMinecraftAdvancements(AdvancementTree advancementManager) {
        if (advancementManager == null) {
            LOGGER.warn("AdvancementManager is null");

        }

        // 收集所有minecraft成就ID
        Set<ResourceLocation> minecraftAdvancements = new HashSet<>();
        for (AdvancementNode advancement : advancementManager.nodes()) {
            ResourceLocation id = advancement.holder().id();
            if(!Objects.equals(id.getNamespace(), MOD_ID)){
                minecraftAdvancements.add(id);
            }
        }

        LOGGER.info("Found {} Minecraft advancements to remove", minecraftAdvancements.size());

        // 移除所有minecraft成就
        advancementManager.remove(minecraftAdvancements);
    }

    /**
     * 移除特定命名空间的成就
     *
     * @param advancementManager 成就管理器
     * @param namespace          命名空间（如："minecraft", "mod"等）
     */
    public static void removeAdvancementsByNamespace(AdvancementTree advancementManager, String namespace) {
        if (advancementManager == null || namespace == null || namespace.isEmpty()) {
            return;
        }

        Set<ResourceLocation> advancementsToRemove = new HashSet<>();
        for (AdvancementNode advancement : advancementManager.nodes()) {
            ResourceLocation id = advancement.holder().id();
            if (namespace.equals(id.getNamespace())) {
                advancementsToRemove.add(id);
            }
        }

        LOGGER.info("Removing {} advancements from namespace '{}'",
                advancementsToRemove.size(), namespace);

        advancementManager.remove(advancementsToRemove);

    }
}