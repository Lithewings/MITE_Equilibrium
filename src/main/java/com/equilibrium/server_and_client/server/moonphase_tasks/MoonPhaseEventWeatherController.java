package com.equilibrium.server_and_client.server.moonphase_tasks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ServerLevelData;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_BLOOD_MOON_THUNDER;
import static com.equilibrium.server_and_client.server.moonphase_tasks.MoonPhaseEventEntitySpawner.spawnLighteningNearPlayer;

public class MoonPhaseEventWeatherController {
    public static void controlWeather(ServerLevel world) {
        if(getGameBooleanRuleFromServer(ENABLE_BLOOD_MOON_THUNDER,world.getServer())){
            clearWeather(world);
        }


        Player playerEntity = world.getRandomPlayer();
        long timeOfDay = world.getDayTime() % 24000; // 获取当前时间（一天有24000刻）

        if (timeOfDay >= 0 && timeOfDay < 14000) {
            startThunderstorm(world);
            if (playerEntity != null)
                if (playerEntity.getRandom().nextInt(4) == 0) {
//                    playerEntity.sendMessage(Text.of("雷声"));
                    playerEntity.level().playSound(null, BlockPos.containing(playerEntity.position()), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 1.0F, 1.0F);
                } else {
                    spawnLighteningNearPlayer(world, playerEntity);
//                    playerEntity.sendMessage(Text.of("雷电"));
                }


        } else { // 其他时间段
            clearWeather(world);
        }
    }

    public static void startThunderstorm(ServerLevel world) {
        ServerLevelData worldProperties = (ServerLevelData) world.getLevelData();
        worldProperties.setThundering(true);
        worldProperties.setRaining(true);
        worldProperties.setClearWeatherTime(0);
        worldProperties.setRainTime(6000);
        worldProperties.setThunderTime(6000);


    }

    public static void clearWeather(ServerLevel world) {
        ServerLevelData worldProperties = (ServerLevelData) world.getLevelData();
        worldProperties.setThundering(false); // 关闭雷雨
        worldProperties.setRaining(false); // 关闭降雨
        worldProperties.setClearWeatherTime(12000); // 设置晴天时间长度（可以根据需要调整）
    }

    public static void clearWeatherForSometime(ServerLevel world, int clearTime) {
        ServerLevelData worldProperties = (ServerLevelData) world.getLevelData();
        worldProperties.setThundering(false); // 关闭雷雨
        worldProperties.setRaining(false); // 关闭降雨
        worldProperties.setClearWeatherTime(clearTime); // 设置晴天时间长度（可以根据需要调整）
    }
}
