package com.equilibrium.server_and_client.server.event;

import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class OnCraftingMetalPickAxe {
    //合成金属镐监听器
    public static ActionResult onCraftingMetalPickAxe(World world, PlayerEntity player) {
        StateSaverAndLoader serverState;
        if (!world.isClient()) {
            serverState = StateSaverAndLoader.getServerState(world.getServer());
        } else {
            return ActionResult.PASS;
        }
        boolean craftedIronPickaxe = serverState.isPickAxeCrafted;

        if (!craftedIronPickaxe) {
            if (!world.isClient()) {
                serverState.isPickAxeCrafted = true;
                player.sendMessage(Text.of("你第一次合成了金属镐"));
            } else {
                return ActionResult.PASS;
            }
        } else {
            // 多次合成时的处理（这里保持原逻辑，返回PASS）
            if (!world.isClient()) {
                // player.sendMessage(Text.of("你多次合成了铁镐"));
                return ActionResult.PASS;
            } else {
                return ActionResult.PASS;
            }
        }
        return ActionResult.PASS;
    }
    public static int isPickAxeCrafted(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(server);
        boolean isVillageCanGenerate = serverState.isPickAxeCrafted && (context.getSource().getWorld().getTimeOfDay()) / 24000L >= 10;
        if (isVillageCanGenerate) {
            if (context.getSource().getEntity().isPlayer())
                context.getSource().getEntity().sendMessage(Text.of("Village now can be generated."));
        } else {
            if (context.getSource().getEntity().isPlayer())
                context.getSource().getEntity().sendMessage(Text.of("Village can not be generated yet."));
        }
        return 1;
    }
}
