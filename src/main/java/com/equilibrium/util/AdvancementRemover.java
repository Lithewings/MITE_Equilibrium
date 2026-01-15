package com.equilibrium.util;

import com.equilibrium.OnServerInitialize;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static void removeAllMinecraftAdvancements(AdvancementManager advancementManager) {
        if (advancementManager == null) {
            LOGGER.warn("AdvancementManager is null");

        }

        // 收集所有minecraft成就ID
        Set<Identifier> minecraftAdvancements = new HashSet<>();
        for (PlacedAdvancement advancement : advancementManager.getAdvancements()) {
            Identifier id = advancement.getAdvancementEntry().id();
            if(!Objects.equals(id.getNamespace(), MOD_ID)){
                minecraftAdvancements.add(id);
            }
        }

        LOGGER.info("Found {} Minecraft advancements to remove", minecraftAdvancements.size());

        // 移除所有minecraft成就
        advancementManager.removeAll(minecraftAdvancements);
    }

    /**
     * 移除特定命名空间的成就
     *
     * @param advancementManager 成就管理器
     * @param namespace          命名空间（如："minecraft", "mod"等）
     */
    public static void removeAdvancementsByNamespace(AdvancementManager advancementManager, String namespace) {
        if (advancementManager == null || namespace == null || namespace.isEmpty()) {
            return;
        }

        Set<Identifier> advancementsToRemove = new HashSet<>();
        for (PlacedAdvancement advancement : advancementManager.getAdvancements()) {
            Identifier id = advancement.getAdvancementEntry().id();
            if (namespace.equals(id.getNamespace())) {
                advancementsToRemove.add(id);
            }
        }

        LOGGER.info("Removing {} advancements from namespace '{}'",
                advancementsToRemove.size(), namespace);

        advancementManager.removeAll(advancementsToRemove);

    }
}