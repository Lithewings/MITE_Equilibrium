package com.equilibrium.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;

public class CommandExecutor {
    public static void executeCommandWithSlash(MinecraftServer server, String command) {
        ServerCommandSource commandSource = server.getCommandSource();
        server.getCommandManager().executeWithPrefix(commandSource,command);
    }
}
