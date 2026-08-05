package com.equilibrium;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

// 全局配置数据类（包含所有模组配置，此处添加破坏锁定字段）
public class GlobalModConfig {
    // 破坏锁定功能开关，默认开启
    public boolean enableBreakLock = true;

    public boolean enableSleepChunksAlwaysLoading = true;

    public boolean enableAutoCrafting = true;

    public boolean enableShowDamage = false;

    // 静态实例，全局唯一
    private static GlobalModConfig INSTANCE;
    // 总配置文件路径：
    private static final File GLOBAL_CONFIG_FILE = FabricLoader.getInstance().getConfigDir()
            .resolve("mite_equilibrium.json").toFile();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // 初始化配置（在onServerInitialized中调用）
    public static void initConfig() {
        // 配置文件不存在则创建默认配置
        if (!GLOBAL_CONFIG_FILE.exists()) {
            INSTANCE = new GlobalModConfig();
            saveConfig();
            return;
        }

        // 读取现有配置文件
        try (FileReader reader = new FileReader(GLOBAL_CONFIG_FILE)) {
            INSTANCE = GSON.fromJson(reader, GlobalModConfig.class);
            // 兼容配置缺失/损坏的情况
            if (INSTANCE == null) {
                INSTANCE = new GlobalModConfig();
                saveConfig();
            }
        } catch (IOException e) {
            throw new RuntimeException("读取全局配置文件失败", e);
        }
    }

    // 保存配置到文件
    private static void saveConfig() {
        try (FileWriter writer = new FileWriter(GLOBAL_CONFIG_FILE)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            throw new RuntimeException("保存全局配置文件失败", e);
        }
    }

    // 获取配置实例（确保已初始化）
    public static GlobalModConfig getInstance() {
        if (INSTANCE == null) {
            initConfig();
        }
        return INSTANCE;
    }

    // 对外提供破坏锁定功能的开关判断
    public static boolean isBreakLockEnabled() {
        return getInstance().enableBreakLock;
    }    // 对外提供破坏锁定功能的开关判断
    public static boolean isSleepChunksAlwaysLoading() {
        return getInstance().enableSleepChunksAlwaysLoading;
    }
    public static boolean isAutoCraftingEnabled() {
        return getInstance().enableAutoCrafting;
    }
    public static boolean isShowDamageEnabled() {
        return getInstance().enableShowDamage;
    }
}