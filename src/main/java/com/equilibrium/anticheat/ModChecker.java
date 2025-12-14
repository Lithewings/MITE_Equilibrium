package com.equilibrium.anticheat;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

import java.util.HashSet;
import java.util.Set;

public class ModChecker implements PreLaunchEntrypoint {

    private static final boolean DOMODCHECK = true;  //是否进行模组检查，方便调试

    private static final Set<String> ALLOWED_MOD_IDS = new HashSet<>() {{
        add("miteequilibrium");   // 自身模组ID（必须和fabric.mod.json中的id一致）
        add("fabric-api");   // Fabric API的官方模组ID
    }};   //模组白名单

    @Override
    public void onPreLaunch() {
        if(!DOMODCHECK) return;

        Set<ModContainer> loadedMods = new HashSet<>(FabricLoader.getInstance().getAllMods());
        Set<String> forbiddenMods = new HashSet<>();

        for (ModContainer mod : loadedMods) {
            String modId = mod.getMetadata().getId();
            if (!ALLOWED_MOD_IDS.contains(modId)) {
                forbiddenMods.add(modId);
            }
        }

        if (!forbiddenMods.isEmpty()) {
            throw new RuntimeException(
                    "\n=====检测到未授权的模组TAT=====\n" +
                            "未授权的模组：" + forbiddenMods + "\n" +
                            "已授权的模组：" + ALLOWED_MOD_IDS + "\n" +
                            "游戏将强制崩溃以阻止未授权模组加载！"
            );
        }
    }
}