package com.equilibrium.server_and_client.server.command;

import com.equilibrium.server_and_client.server.EventOnServerInitOrRunning;
import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
import com.equilibrium.util.BooleanStorageUtil;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;

import java.nio.file.Path;

import static com.equilibrium.util.BooleanStorageUtil.loadWorldInformation;
import static net.minecraft.world.World.OVERWORLD;

public class ServerCommands {
    // 注册命令的标准方式，适配 CommandDispatcher 的签名
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
                CommandManager.literal("village")
                        .executes
                                (EventOnServerInitOrRunning::isPickAxeCrafted)

        );
        dispatcher.register(
                CommandManager.literal("deathTime")
                        .executes(context -> {
                            PlayerEntity player = context.getSource().getPlayer();
                            StateSaverAndLoader stateSaverAndLoader = StateSaverAndLoader.getServerState(context.getSource().getServer());
                            if(player!=null)
                                player.sendMessage(Text.of("你的总死亡次数为: " + stateSaverAndLoader.playerDeathTimes));
                            return 1;
                        })
        );
        dispatcher.register(
                CommandManager.literal("checkAdvancement")
                        .executes(context -> {
                            //请确保世界存在
                            PlayerEntity player = context.getSource().getPlayer();
                            long originalSeed = context.getSource().getServer().getWorld(OVERWORLD).getSeed();
                            Path path = context.getSource().getServer().getSavePath(WorldSavePath.ROOT).normalize().resolve("WorldInformationRecorder.dat");;
                            BooleanStorageUtil.WorldInformationRecorder worldInformationRecorder = loadWorldInformation(path.toString());
                            if(worldInformationRecorder!=null){
                                int day = worldInformationRecorder.getFinishDay();
                                long seed = worldInformationRecorder.getSeed();
                                String version = worldInformationRecorder.getVersion();

                                if(day>=0 && seed==originalSeed){
                                    player.sendMessage(Text.of("通关天数为: " + day));
                                    player.sendMessage(Text.of("世界种子为: " + seed));
                                    player.sendMessage(Text.of("版本信息号为: " + version));
                                }
                                else
                                    player.sendMessage(Text.of("无效的通关信息"));

                            }
                            else
                                player.sendMessage(Text.of("未获取到通关信息"));
                            return 1;
                        })
        );

    }
}
