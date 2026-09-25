package com.equilibrium.server_and_client.fog_weather_event;

import static com.equilibrium.common_gamerules.GameRuleGetter.booleanGameRuleGetterFromServer;
import static com.equilibrium.common_gamerules.GameRuleRegister.IS_FOG_WEATHER_NOW;
import static com.equilibrium.server_and_client.fog_weather_event.FogWeatherHandler.FOG_WEATHER_POSSIBILITY;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;


public class FogWeatherMediator {


    private static boolean samplingFrequencyEnabled(long time) {
        return time % 80L == 0;
    }

    private static boolean samplingFrequencyForServerTrySwitch(long time) {
        return time % 24000L == 0;
    }

    //第一天不生效
    private static boolean isValidCircumstanceForClient(long time){
        return samplingFrequencyEnabled(time) && time > 24000L;
    }

    //第一天不生效
    private static boolean isValidCircumstanceForServer(long time){
        return samplingFrequencyForServerTrySwitch(time) && time > 24000L;
    }


    //MainEntryPoint,客户端接收每一个tick都执行
    public static void synchronizeFogWeatherIfAvailable(ClientLevel clientWorld){
        if(isValidCircumstanceForClient(clientWorld.getDayTime())){
            FogWeatherHandler fogWeatherHandler = new FogWeatherHandler(clientWorld);
            fogWeatherHandler.situationSwitch();
        }
    }
    //MainEntryPoint,服务端负责修改
    public static void addFogRandomly(ServerLevel serverWorld){
        if(isValidCircumstanceForServer(serverWorld.getDayTime())){
            boolean isFogNow = booleanGameRuleGetterFromServer(serverWorld.getServer(),IS_FOG_WEATHER_NOW);
            boolean tryAddFog = serverWorld.getRandom().nextFloat()<=FOG_WEATHER_POSSIBILITY;

            //如果是雾天,判断应该加雾,则不变
            //如果是雾天,判断不应该加雾气,则切换天气到晴天
            //如果是晴天,判断应该加雾,则切换天气到雾天
            //如果是晴天,判断不应该加雾,则不变



            // 如果是雾天，但这次判断不应该加雾：切换回晴天
            if (isFogNow && !tryAddFog) {
                serverWorld.getGameRules().getRule(IS_FOG_WEATHER_NOW).set(false, serverWorld.getServer());
            }

            // 如果是晴天，但这次判断应该加雾：切换为雾天
            if (!isFogNow && tryAddFog) {
                serverWorld.getGameRules().getRule(IS_FOG_WEATHER_NOW).set(true, serverWorld.getServer());
            }

            // 其余情况：
            // 雾天 + 应该加雾 -> 不变
            // 晴天 + 不应该加雾 -> 不变

            //输出本次的服务端计算结果
            boolean isFogNowAfterCal = booleanGameRuleGetterFromServer(serverWorld.getServer(),IS_FOG_WEATHER_NOW);
            String weatherName = isFogNowAfterCal ? "雾天" : "晴天";
            String weatherInfo = String.format("今天的天气是: %s", weatherName);
            serverWorld.getServer().getPlayerList().getPlayers()
                    .forEach(serverPlayerEntity -> serverPlayerEntity.sendSystemMessage(Component.nullToEmpty(weatherInfo)));

        }

    }
}
