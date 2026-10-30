package com.equilibrium.difficulty_entry_screen;

import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.UUID;

public final class DifficultyEntryScreenMediator {


    private static final Text TITLE = Text.literal("Difficulty Entries");


    private DifficultyEntryScreenMediator() {
    }


    public static boolean openFor(ServerPlayerEntity player) {
        if (player == null || player.isRemoved()) {
            return false;
        }
        // 原版开屏流程：服务端建 handler →  OpenScreenS2CPacket → 客户端按屏幕种类查 HandledScreens 映射
        OptionalInt syncId = player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                (id, inventory, p) -> new DifficultyEntryScreenHandler(id, inventory,
                        ScreenHandlerContext.create(p.getWorld(), p.getBlockPos())),
                TITLE));
        return syncId.isPresent();
    }


}
