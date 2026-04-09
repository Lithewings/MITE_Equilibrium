package com.equilibrium.server_and_client.server.moonphase_tasks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.level.ServerWorldProperties;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.*;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_BLOOD_MOON_THUNDER;
import static com.equilibrium.server_and_client.server.moonphase_tasks.MoonPhaseEventEntitySpawner.spawnLighteningNearPlayer;

public class MoonPhaseEventWeatherController {
    public static void controlWeather(ServerWorld world) {
        if(getGameBooleanRuleFromServer(ENABLE_BLOOD_MOON_THUNDER,world.getServer())){
            clearWeather(world);
        }


        PlayerEntity playerEntity = world.getRandomAlivePlayer();
        long timeOfDay = world.getTimeOfDay() % 24000; // 获取当前时间（一天有24000刻）

        if (timeOfDay >= 0 && timeOfDay < 14000) {
            startThunderstorm(world);
            if (playerEntity != null)
                if (playerEntity.getRandom().nextInt(4) == 0) {
//                    playerEntity.sendMessage(Text.of("雷声"));
                    playerEntity.getWorld().playSound(null, BlockPos.ofFloored(playerEntity.getPos()), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 1.0F, 1.0F);
                } else {
                    spawnLighteningNearPlayer(world, playerEntity);
//                    playerEntity.sendMessage(Text.of("雷电"));
                }


        } else { // 其他时间段
            clearWeather(world);
        }
    }

    public static void startThunderstorm(ServerWorld world) {
        ServerWorldProperties worldProperties = (ServerWorldProperties) world.getLevelProperties();
        worldProperties.setThundering(true);
        worldProperties.setRaining(true);
        worldProperties.setClearWeatherTime(0);
        worldProperties.setRainTime(6000);
        worldProperties.setThunderTime(6000);


    }

    public static void clearWeather(ServerWorld world) {
        ServerWorldProperties worldProperties = (ServerWorldProperties) world.getLevelProperties();
        worldProperties.setThundering(false); // 关闭雷雨
        worldProperties.setRaining(false); // 关闭降雨
        worldProperties.setClearWeatherTime(12000); // 设置晴天时间长度（可以根据需要调整）
    }

    public static void clearWeatherForSometime(ServerWorld world, int clearTime) {
        ServerWorldProperties worldProperties = (ServerWorldProperties) world.getLevelProperties();
        worldProperties.setThundering(false); // 关闭雷雨
        worldProperties.setRaining(false); // 关闭降雨
        worldProperties.setClearWeatherTime(clearTime); // 设置晴天时间长度（可以根据需要调整）
    }
}
