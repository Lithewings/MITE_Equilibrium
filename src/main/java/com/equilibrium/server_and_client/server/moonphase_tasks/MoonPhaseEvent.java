package com.equilibrium.server_and_client.server.moonphase_tasks;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_CROP_ILLNESS;
import static com.equilibrium.server_and_client.server.event.CropIllnessEvent.applyIllnessForCrop;
import static com.equilibrium.server_and_client.server.moonphase_tasks.MoonPhaseEventEntitySpawner.*;
import static com.equilibrium.server_and_client.server.moonphase_tasks.MoonPhaseEventWeatherController.clearWeatherForSometime;
import static com.equilibrium.server_and_client.server.moonphase_tasks.MoonPhaseEventWeatherController.controlWeather;

public class MoonPhaseEvent {


    public static String getMoonType(Level world) {
        String moonType = WorldMoonPhasesSelector.calculateMoonType(world);
        if (moonType != null)
            return moonType;
        else {
            return "errorMoontype";
        }
    }

    @NotNull
    public static ServerLevel moonPhaseEvent(MinecraftServer server) {
        //月相事件
        String moonType = getMoonType(server.getLevel(Level.OVERWORLD));
        ServerLevel serverOverWorld = server.getLevel(Level.OVERWORLD);
        boolean isNoPlayersInTheOverWorld = serverOverWorld.players().isEmpty();
        Random random = new Random();


        if (isNoPlayersInTheOverWorld) {
            if (serverOverWorld.getGameRules().getInt(GameRules.RULE_RANDOMTICKING) != 3) {
//                    for (PlayerEntity player : server.getPlayerManager().getPlayerList())
//                        player.sendMessage(Text.of("由于主世界没有玩家,随机刻速度已回调至默认值"), true);
                //有可能目前是蓝月,但玩家在地底世界,所以会陷入这里恢复默认,但蓝月那边又改成5,这样反复执行了这段代码
                RandomTickModifier(serverOverWorld, 3);
            }

        }

        if (Objects.equals(moonType, "errorMoontype"))
            for (Player player : server.getPlayerList().getPlayers())
                player.displayClientMessage(Component.nullToEmpty("月相加载失败"), true);
        else {
            //月相事件,只在主世界进行
            //增大随机刻的条件
            boolean shouldRandomTickIncrease = (moonType.equals("blueMoon") || (moonType.equals("harvestMoon")) || (moonType.equals("haloMoon")));
            if (!shouldRandomTickIncrease) {
                if (serverOverWorld.getGameRules().getInt(GameRules.RULE_RANDOMTICKING) != 3) {
//                        for (PlayerEntity player : server.getPlayerManager().getPlayerList())
//                            player.sendMessage(Text.of("由于处在普通月相,随机刻已回调至默认值"), true);
                    RandomTickModifier(serverOverWorld, 3);
                }
            }


            if (moonType.equals("bloodMoon")) {
                if (serverOverWorld.getDayTime() % 100 == 0) {
                    //执行间隔事件
                    spawnMobNearPlayer(serverOverWorld);

                }
                if (serverOverWorld.getDayTime() % random.nextInt(50, 64) == 0) {
                    //执行间隔事件
                    controlWeather(serverOverWorld);
//                        this.sendMessage(Text.of("雷电事件"));
                }
                if (serverOverWorld.getDayTime() % 64 == 0) {
                    //根据游戏规则,判断是否应该施加作物疾病
                    if(getGameBooleanRuleFromServer(ENABLE_CROP_ILLNESS,server))
                        applyIllnessForCrop(serverOverWorld);
                }


            }


            if (moonType.equals("harvestMoon") || (moonType.equals("haloMoon"))) {
                if (serverOverWorld.getGameRules().getInt(GameRules.RULE_RANDOMTICKING) != 4)
                    RandomTickModifier(serverOverWorld, 4);
//               if (this.age % 100 == 0) {
                //执行间隔事件
//               this.sendMessage(Text.of("黄月/幻月升起,触发事件"));
//               }
            }

            if (moonType.equals("fullMoon")) {
                if (serverOverWorld.getDayTime() % 100 == 0) {
//              this.sendMessage(Text.of("满月升起,触发事件"));
                    applyStrengthToHostileMobs(serverOverWorld);
                }
            }

            if (moonType.equals("newMoon")) {
                if (serverOverWorld.getDayTime() % 100 == 0) {
                    applyWeaknessToHostileMobs(serverOverWorld);
//              this.sendMessage(Text.of("新月升起,触发事件"));
                }
            }

            //第一次蓝月,不改变随机刻速度
            if (moonType.equals("blueMoon")) {

                clearWeatherForSometime(serverOverWorld,12000);

                if (serverOverWorld.getDayTime() > 24000) {

                    if (serverOverWorld.getGameRules().getInt(GameRules.RULE_RANDOMTICKING) != 5)
                        RandomTickModifier(serverOverWorld, 5);
                    if (serverOverWorld.getDayTime() % 1200 == 0) {

//								this.sendMessage(Text.of("蓝月升起,触发事件"));
                        //执行间隔事件
                        spawnAnimalNearPlayer(serverOverWorld);
                    }
                } else {
                    if (serverOverWorld.getGameRules().getInt(GameRules.RULE_RANDOMTICKING) != 3) {
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

    public static void RandomTickModifier(ServerLevel world, int randomTickSpeed) {
        world.getGameRules().getRule(GameRules.RULE_RANDOMTICKING).set(randomTickSpeed,world.getServer());
    }



}
