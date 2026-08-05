package com.equilibrium;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.neoforged.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class GlobalModConfig {
    private static final Path CONFIG_DIR = FMLPaths.CONFIGDIR.get();
    private static final File GLOBAL_CONFIG_FILE = CONFIG_DIR.resolve("mite_equilibrium.json").toFile();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static GlobalModConfig INSTANCE;

    public boolean enableBreakLock = true;
    public boolean enableSleepChunksAlwaysLoading = true;
    public boolean enableAutoCrafting = true;
    public boolean enableShowDamage = false;

    public static void initConfig() {
        if (!GLOBAL_CONFIG_FILE.exists()) {
            INSTANCE = new GlobalModConfig();
            saveConfig();
            return;
        }

        try (FileReader reader = new FileReader(GLOBAL_CONFIG_FILE)) {
            INSTANCE = GSON.fromJson(reader, GlobalModConfig.class);
            if (INSTANCE == null) {
                INSTANCE = new GlobalModConfig();
                saveConfig();
            }
        } catch (IOException e) {
            throw new RuntimeException("读取全局配置文件失败", e);
        }
    }

    private static void saveConfig() {
        try (FileWriter writer = new FileWriter(GLOBAL_CONFIG_FILE)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            throw new RuntimeException("保存全局配置文件失败", e);
        }
    }

    public static GlobalModConfig getInstance() {
        if (INSTANCE == null) {
            initConfig();
        }
        return INSTANCE;
    }

    public static boolean isBreakLockEnabled() {
        return getInstance().enableBreakLock;
    }

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