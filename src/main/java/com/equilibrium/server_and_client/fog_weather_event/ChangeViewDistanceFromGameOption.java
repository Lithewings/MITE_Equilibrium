package com.equilibrium.server_and_client.fog_weather_event;


import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

/**
 * 渲染距离(GameOptions.getViewDistance)的统一读写入口。
 * Handler 只做决策，具体访问游戏选项的动作都收敛在这里，避免读写路径被复制到多处。
 */
public class ChangeViewDistanceFromGameOption {
    private static Minecraft getClientInstance() {
        return Minecraft.getInstance();
    }
    private static Options getGameOption() {
        return getClientInstance().options;
    }

    /** 当前渲染距离 */
    public static int getViewDistance() {
        return getGameOption().renderDistance().get();
    }

    /** 设置渲染距离 */
    public static void changeViewDistance(int distance) {
        getGameOption().renderDistance().set(distance);
    }

    public static int getSimulationDistance(){
        return getGameOption().simulationDistance().get();
    }

}
