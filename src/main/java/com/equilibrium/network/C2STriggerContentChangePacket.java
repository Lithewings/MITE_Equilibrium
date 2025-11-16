package com.equilibrium.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static com.equilibrium.MITEequilibrium.MOD_ID;

public class C2STriggerContentChangePacket {
    public static final Identifier PACKET_ID = Identifier.of(MOD_ID, "trigger_content_change");

    public static void register() {
        PayloadTypeRegistry.playC2S().register(TriggerContentChangePayload.ID, TriggerContentChangePayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(TriggerContentChangePayload.ID,
                (payload, context) -> {
                    ServerPlayerEntity player = context.player();
                    context.server().execute(() -> {
                        triggerContentChange(player);
                    });
                });
    }

    private static void triggerContentChange(ServerPlayerEntity player) {
        ScreenHandler screenHandler = player.currentScreenHandler;

        if (screenHandler instanceof CraftingScreenHandler craftingHandler) {

            // 方法2：直接调用 onContentChanged
            craftingHandler.onContentChanged(craftingHandler.input);

//            System.out.println("已触发合成台内容变化事件");
        }
    }



    // Payload 定义
    public static class TriggerContentChangePayload implements CustomPayload {
        public static final CustomPayload.Id<TriggerContentChangePayload> ID =
                new CustomPayload.Id<>(PACKET_ID);

        public TriggerContentChangePayload() {
            // 空构造，不需要数据
        }

        public static final PacketCodec<PacketByteBuf, TriggerContentChangePayload> CODEC =
                PacketCodec.of(
                        (payload, buf) -> {
                            // 不需要写入数据
                        },
                        buf -> new TriggerContentChangePayload()
                );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    // 客户端发送方法
    public static void sendTrigger() {
        ClientPlayNetworking.send(new TriggerContentChangePayload());
    }
}