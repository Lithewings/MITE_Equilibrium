package com.equilibrium.server_and_client.server.moonphase_tasks;

import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.*;

import net.minecraft.world.level.ServerWorldProperties;
import org.jetbrains.annotations.NotNull;

import static com.equilibrium.DifficultyEntryOnGameRules.*;
import static com.equilibrium.server_and_client.server.CropIllnessEvent.applyIllnessForCrop;
import static com.equilibrium.server_and_client.server.moonphase_tasks.MoonPhaseEventEntitySpawner.*;
import static com.equilibrium.server_and_client.server.moonphase_tasks.MoonPhaseEventWeatherController.controlWeather;

public class MoonPhaseEvent {


    public static String getMoonType(World world) {
        String moonType = WorldMoonPhasesSelector.calculateMoonType(world);
        if (moonType != null)
            return moonType;
        else {
            return "errorMoontype";
        }
    }

    @NotNull
    public static ServerWorld moonPhaseEvent(MinecraftServer server) {
        //月相事件
        String moonType = getMoonType(server.getWorld(World.OVERWORLD));
        ServerWorld serverOverWorld = server.getWorld(World.OVERWORLD);
        boolean isNoPlayersInTheOverWorld = serverOverWorld.getPlayers().isEmpty();
        Random random = new Random();


        if (isNoPlayersInTheOverWorld) {
            if (serverOverWorld.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED) != 3) {
//                    for (PlayerEntity player : server.getPlayerManager().getPlayerList())
//                        player.sendMessage(Text.of("由于主世界没有玩家,随机刻速度已回调至默认值"), true);
                //有可能目前是蓝月,但玩家在地底世界,所以会陷入这里恢复默认,但蓝月那边又改成5,这样反复执行了这段代码
                RandomTickModifier(serverOverWorld, 3);
            }

        }

        if (Objects.equals(moonType, "errorMoontype"))
            for (PlayerEntity player : server.getPlayerManager().getPlayerList())
                player.sendMessage(Text.of("月相加载失败"), true);
        else {
            //月相事件,只在主世界进行
            //增大随机刻的条件
            boolean shouldRandomTickIncrease = (moonType.equals("blueMoon") || (moonType.equals("harvestMoon")) || (moonType.equals("haloMoon")));
            if (!shouldRandomTickIncrease) {
                if (serverOverWorld.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED) != 3) {
//                        for (PlayerEntity player : server.getPlayerManager().getPlayerList())
//                            player.sendMessage(Text.of("由于处在普通月相,随机刻已回调至默认值"), true);
                    RandomTickModifier(serverOverWorld, 3);
                }
            }


            if (moonType.equals("bloodMoon")) {
                if (serverOverWorld.getTimeOfDay() % 100 == 0) {
                    //执行间隔事件
                    spawnMobNearPlayer(serverOverWorld);

                }
                if (serverOverWorld.getTimeOfDay() % random.nextInt(50, 64) == 0) {
                    //执行间隔事件
                    controlWeather(serverOverWorld);
//                        this.sendMessage(Text.of("雷电事件"));
                }
                if (serverOverWorld.getTimeOfDay() % 64 == 0) {
                    //根据游戏规则,判断是否应该施加作物疾病
                    if(getGameBooleanRuleFromServer(ENABLE_CROP_ILLNESS,server))
                        applyIllnessForCrop(serverOverWorld);
                }


            }


            if (moonType.equals("harvestMoon") || (moonType.equals("haloMoon"))) {
                if (serverOverWorld.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED) != 4)
                    RandomTickModifier(serverOverWorld, 4);
//               if (this.age % 100 == 0) {
                //执行间隔事件
//               this.sendMessage(Text.of("黄月/幻月升起,触发事件"));
//               }
            }

            if (moonType.equals("fullMoon")) {
                if (serverOverWorld.getTimeOfDay() % 100 == 0) {
//              this.sendMessage(Text.of("满月升起,触发事件"));
                    applyStrengthToHostileMobs(serverOverWorld);
                }
            }

            if (moonType.equals("newMoon")) {
                if (serverOverWorld.getTimeOfDay() % 100 == 0) {
                    applyWeaknessToHostileMobs(serverOverWorld);
//              this.sendMessage(Text.of("新月升起,触发事件"));
                }
            }

            //第一次蓝月,不改变随机刻速度
            if (moonType.equals("blueMoon")) {
                if (serverOverWorld.getTimeOfDay() > 24000) {

                    if (serverOverWorld.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED) != 5)
                        RandomTickModifier(serverOverWorld, 5);
                    if (serverOverWorld.getTimeOfDay() % 1200 == 0) {

//								this.sendMessage(Text.of("蓝月升起,触发事件"));
                        //执行间隔事件
                        spawnAnimalNearPlayer(serverOverWorld);
                    }
                } else {
                    if (serverOverWorld.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED) != 3) {
//                            for (PlayerEntity player : server.getPlayerManager().getPlayerList())
//                                player.sendMessage(Text.of("由于第一天的蓝月并没有随机刻增益,随机刻应该修改为3"), true);
                        RandomTickModifier(serverOverWorld, 3);
                    }
                }
                //应该是用world.找到所有玩家,这里无非就是避免客户端世界直接转服务器世界造成崩溃
                //待改进:应该是this.getWorld,如果不是客户端世界再执行spawnAnimal方法

            }
        }
        return serverOverWorld;
    }

    public static void RandomTickModifier(ServerWorld world, int randomTickSpeed) {
        world.getGameRules().get(GameRules.RANDOM_TICK_SPEED).set(randomTickSpeed,world.getServer());
    }



}
