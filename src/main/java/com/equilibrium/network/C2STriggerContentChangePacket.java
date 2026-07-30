package com.equilibrium.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class C2STriggerContentChangePacket {

    public static final ResourceLocation TRIGGER_CONTENT_CHANGE_PACKET_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "trigger_content_change");

    public static void registerOnServer() {
        PayloadTypeRegistry.playC2S().register(TriggerContentChangePayload.ID, TriggerContentChangePayload.CODEC);
        packetReceive();
    }



    private static void packetReceive() {
        ServerPlayNetworking.registerGlobalReceiver(TriggerContentChangePayload.ID,
                (payload, context) -> {
                    ServerPlayer player = context.player();
                    context.server().execute(() -> {
                        triggerContentChange(player);
                    });
                });
    }


    private static void triggerContentChange(ServerPlayer player) {
        AbstractContainerMenu screenHandler = player.containerMenu;

        if (screenHandler instanceof CraftingMenu craftingHandler) {

            // 方法2：直接调用 onContentChanged
            craftingHandler.slotsChanged(craftingHandler.craftSlots);

//            System.out.println("已触发合成台内容变化事件");
        }
        if (screenHandler instanceof InventoryMenu playerScreenHandler) {

            // 方法2：直接调用 onContentChanged
            playerScreenHandler.slotsChanged(null);

//            System.out.println("已触发合成台内容变化事件");
        }
    }



    // Payload 定义
    public static class TriggerContentChangePayload implements CustomPacketPayload {
        public static final Type<TriggerContentChangePayload> ID =
                new Type<>(TRIGGER_CONTENT_CHANGE_PACKET_ID);

        public TriggerContentChangePayload() {
            // 空构造，不需要数据
        }

        public static final StreamCodec<FriendlyByteBuf, TriggerContentChangePayload> CODEC =
                StreamCodec.ofMember(
                        (payload, buf) -> {
                            // 不需要写入数据
                        },
                        buf -> new TriggerContentChangePayload()
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return ID;
        }
    }

    // 客户端发送方法
    public static void sendTrigger() {
        ClientPlayNetworking.send(new TriggerContentChangePayload());
    }
}