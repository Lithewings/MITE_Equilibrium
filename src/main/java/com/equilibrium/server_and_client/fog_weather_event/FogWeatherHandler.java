package com.equilibrium.server_and_client.fog_weather_event;

import com.equilibrium.common_gamerules.GameRuleGetter;
import net.minecraft.client.world.ClientWorld;

import static com.equilibrium.GlobalModConfig.getFogDissipatedViewDistance;
import static com.equilibrium.common_gamerules.GameRuleRegister.IS_FOG_WEATHER_NOW;
import static com.equilibrium.server_and_client.fog_weather_event.ChangeViewDistanceFromGameOption.changeViewDistance;
import static com.equilibrium.server_and_client.fog_weather_event.ChangeViewDistanceFromGameOption.getSimulationDistance;
import static com.equilibrium.server_and_client.fog_weather_event.IsFogWeatherSuitableUtil.isValid;


/**
 * 客户端雾天视距「适配器」。
 *
 * <p>本类不再持有任何天气状态：它只负责三件事——</p>
 * <ol>
 *   <li>读取服务端权威的 gamerule {@code IS_FOG_WEATHER_NOW}；</li>
 *   <li>把 {@link FogWeatherStateMachine} 给出的 {@link FogWeatherStateMachine.Effect}
 *       翻译成本地渲染距离的读写。</li>
 * </ol>
 *
 * <p><b>权威模型：</b>服务端是唯一权威。客户端只读同步下来的规则并做本地反应，
 * 不接受任何未经验证的 C2S 数据包作为事实来源。</p>
 *
 * <p>真正的状态与转移逻辑全部在 {@link FogWeatherStateMachine} 里，本类不做决策。</p>
 */
public class FogWeatherHandler {

    /**
     * 状态机必须跨采样存活：{@code FogWeatherMediator} 每次采样都会 new 一个 Handler，
     * 所以状态机挂在 static 上，全部玩家共享。
     */
    private static final FogWeatherStateMachine STATE_MACHINE = new FogWeatherStateMachine();

    private final ClientWorld clientWorld;

    //具体由每个玩家设定的值决定
    private final int baseViewDistance;

    public static final int FOG_VIEW_DISTANCE_MIN = 4;
    public static final int FOG_VIEW_DISTANCE_MAX = 8;

    public static final float FOG_WEATHER_POSSIBILITY = 0.5f;



    public FogWeatherHandler(ClientWorld clientWorld) {
        this.clientWorld = clientWorld;
        this.baseViewDistance = getFogDissipatedViewDistance()==-1?getSimulationDistance():getFogDissipatedViewDistance();

    }

    public void situationSwitch() {
        // 世界不适用：回到初始晴天；若之前在别的维度压过视距，这里撤销一次（幂等）。
        if (!isValid(this.clientWorld)) {
            applyEffect(STATE_MACHINE.leaveApplicableWorld());
            return;
        }
        // 适用世界：把服务端同步的目标天气喂给状态机，按返回的效果行动。
        applyEffect(STATE_MACHINE.sampleWeather(isFogShouldEnable()));
    }





    /** 把状态机给出的效果翻译成具体的渲染距离操作——本类唯一的「执行」职责。 */
    private void applyEffect(FogWeatherStateMachine.Effect effect) {
        switch (effect) {
            case APPLY_FOG -> changeViewDistance(getNextRandomDistance());
            case CLEAR_FOG -> changeViewDistance(baseViewDistance);
            case NO_ACTION -> {
                // 稳态：保持玩家当前的渲染距离，不做任何事。
            }
        }
    }

    /** 供调试 / UI 查询当前是否处于雾天。 */
    public static boolean isFoggy() {
        return STATE_MACHINE.isFoggy();
    }

    private boolean isFogShouldEnable() {
        return GameRuleGetter.booleanGameRuleGetterFromClient(clientWorld, IS_FOG_WEATHER_NOW);
    }

    private int getNextRandomDistance() {
        return clientWorld.getRandom().nextBetween(FOG_VIEW_DISTANCE_MIN, FOG_VIEW_DISTANCE_MAX);
    }



}
