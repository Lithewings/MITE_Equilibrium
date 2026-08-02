package com.equilibrium.server_and_client.server.event;

import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class OnCraftingMetalPickAxe {
    //合成金属镐监听器
    public static InteractionResult onCraftingMetalPickAxe(Level world, Player player) {
        StateSaverAndLoader serverState;
        if (!world.isClientSide()) {
            serverState = StateSaverAndLoader.getServerState(world.getServer());
        } else {
            return InteractionResult.PASS;
        }
        boolean craftedIronPickaxe = serverState.isPickAxeCrafted;

        if (!craftedIronPickaxe) {
            if (!world.isClientSide()) {
                serverState.isPickAxeCrafted = true;
                player.sendSystemMessage(Component.nullToEmpty("你第一次合成了金属镐"));
            } else {
                return InteractionResult.PASS;
            }
        } else {
            // 多次合成时的处理（这里保持原逻辑，返回PASS）
            if (!world.isClientSide()) {
                // player.sendMessage(Text.of("你多次合成了铁镐"));
                return InteractionResult.PASS;
            } else {
                return InteractionResult.PASS;
            }
        }
        return InteractionResult.PASS;
    }
    public static int isPickAxeCrafted(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        MinecraftServer server = source.getServer();
        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(server);
        boolean isVillageCanGenerate = serverState.isPickAxeCrafted && (context.getSource().getLevel().getDayTime()) / 24000L >= 10;
        if (isVillageCanGenerate) {
            if (context.getSource().getEntity().isAlwaysTicking())
                context.getSource().getEntity().sendSystemMessage(Component.nullToEmpty("Village now can be generated."));
        } else {
            if (context.getSource().getEntity().isAlwaysTicking())
                context.getSource().getEntity().sendSystemMessage(Component.nullToEmpty("Village can not be generated yet."));
        }
        return 1;
    }
}
