package com.equilibrium.difficulty_entry_screen;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.UUID;

public final class DifficultyEntryScreenMediator {


    private static final Component TITLE = Component.literal("Difficulty Entries");


    private DifficultyEntryScreenMediator() {
    }


    public static boolean openFor(ServerPlayer player) {
        if (player == null || player.isRemoved()) {
            return false;
        }
        // 原版开屏流程：服务端建 handler →  OpenScreenS2CPacket → 客户端按屏幕种类查 HandledScreens 映射
        OptionalInt syncId = player.openMenu(new SimpleMenuProvider(
                (id, inventory, p) -> new DifficultyEntryScreenHandler(id, inventory,
                        ContainerLevelAccess.create(p.level(), p.blockPosition())),
                TITLE));
        return syncId.isPresent();
    }


}
