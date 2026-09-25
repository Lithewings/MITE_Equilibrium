package com.equilibrium.network;

import com.equilibrium.common_gamerules.GameRuleUtil;
import com.equilibrium.difficulty_entry.DifficultyEntryUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.equilibrium.OnServerInitialize.MOD_ID;
import static com.equilibrium.common_gamerules.GameRuleUtil.synchronizeAllBooleanGameRulesTo;

/**
 * C2S：客户端「请求」服务端把当前全部游戏规则重新同步一遍。
 *
 * <p><b>为什么需要它：</b>游戏规则最终写进 {@code ClientWorld.getGameRules()}，
 * 而 {@code ClientWorld} 是「一个世界实例一份」——切维度、死亡重生、重连都会换新实例，
 * 新实例的规则表是<em>默认值</em>。模组目前的同步只在
 * {@code PlayerManagerMixin.onPlayerConnect} 里跑过一次，换实例时不会重跑，
 * 于是客户端会读到默认值，而不是服务端上的真实值。</p>
 *
 * <p><b>为什么安全：</b>这个包裹<em>不携带任何数值</em>，客户端能表达的只有
 * 「请把规则再发一遍」这一句话。发什么完全由服务端从自己的权威规则表里读，
 * 所以即使有人伪造这个包反复请求，也只会拿到服务端已有的真值——改不了任何东西。
 * 这与 {@link S2CGameRuleBooleanSimplePacket} 的「服务端唯一权威」模型一致。</p>
 */
public class C2SRequestGameRuleResyncPacket {



    /** 上一个已请求过同步的世界实例。引用比较即可——「换没换实例」正是身份语义。 */
    private static ClientLevel lastRequestedWorld;

    // ========================================================================
    // 服务端
    // ========================================================================

    public static void registerOnServer() {
        PayloadTypeRegistry.playC2S().register(RequestGameRuleResyncPayload.ID, RequestGameRuleResyncPayload.CODEC);
        packetReceive();
    }
    private static void packetReceive() {
        ServerPlayNetworking.registerGlobalReceiver(RequestGameRuleResyncPayload.ID,
                (payload, context) ->
                        // 切回服务端主线程再动世界数据
                        context.server().execute(() -> {
                            GameRuleUtil.synchronizeAllBooleanGameRulesTo(context.player());
                            DifficultyEntryUtil.onPlayerConnectSynchronizingGameRulesForBoolean(context.player());
                        }));
    }



    // ========================================================================
    // 客户端
    // ========================================================================

    /**
     * 世界实例一换就补一次规则同步。
     *
     * <p>切维度 / 重生 / 重连都会走到这里，而这三条路径都不会触发服务端的
     * {@code onPlayerConnect}，所以必须由客户端主动补请求。</p>
     *
     */
    //每一个tick都会调用
    public static void requestResyncOnWorldInstanceChange(ClientLevel currentWorld) {
        if (currentWorld == null || currentWorld == lastRequestedWorld) {
            return;
        }
        lastRequestedWorld = currentWorld;
        sendRequest();
    }


    public static void sendRequest() {
        ClientPlayNetworking.send(new RequestGameRuleResyncPayload());
    }


    public static class RequestGameRuleResyncPayload implements CustomPacketPayload {

        public static final Type<RequestGameRuleResyncPayload> ID =
                new Type<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "request_game_rule_resync"));

        public RequestGameRuleResyncPayload() {
        }

        public static final StreamCodec<FriendlyByteBuf, RequestGameRuleResyncPayload> CODEC =
                StreamCodec.ofMember(
                        // 编码器：无内容
                        (payload, buf) -> {
                        },
                        // 解码器：无内容
                        buf -> new RequestGameRuleResyncPayload()
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return ID;
        }
    }
}
