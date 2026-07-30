package com.equilibrium.util;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

public class CommandExecutor {
    public static void executeCommandWithSlash(MinecraftServer server, String command) {
        CommandSourceStack commandSource = server.createCommandSourceStack();
        server.getCommands().performPrefixedCommand(commandSource,command);
    }
}
