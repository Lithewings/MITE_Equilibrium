package com.equilibrium.server_and_client.fog_weather_event;

/**
 * 雾天视距状态机（纯逻辑，无任何 Minecraft 依赖，可直接单元测试）。
 *
 * <p>只有两个稳定态：{@link State#SUNNY} 与 {@link State#FOGGY}。
 * 调用方不需要理解「前一采样点 / 当前采样点 / 效果是否已施加」这些内部寄存器，
 * 只需要对机器说两句话：</p>
 * <ul>
 *   <li>还在适用维度里 —— {@link #sampleWeather(boolean)}，把服务端同步的目标天气告诉它；</li>
 *   <li>离开了适用维度 —— {@link #leaveApplicableWorld()}。</li>
 * </ul>
 * <p>然后按返回的 {@link Effect} 执行对应动作即可。</p>
 *
 * <p><b>幂等保证：</b>{@link #leaveApplicableWorld()} 只在「确实施加过雾天效果」时
 * 返回 {@link Effect#CLEAR_FOG} 一次；连续调用不会重复触发。</p>
 */
public final class FogWeatherStateMachine {

    /** 两个稳定态。 */
    public enum State {
        SUNNY, FOGGY
    }

    /** 状态机给调用方的「该做什么」指令。 */
    public enum Effect {
        /** 无需动作：保持当前渲染距离不变。 */
        NO_ACTION,
        /** 进入雾天：请把渲染距离压暗。 */
        APPLY_FOG,
        /** 离开雾天 / 失效：请恢复渲染距离。 */
        CLEAR_FOG
    }

    /** 上一个采样点判定的稳态。 */
    private State stateBefore = State.SUNNY;
    /** 当前采样点判定的稳态。 */
    private State stateNow = State.SUNNY;
    /** 雾天视距是否已施加——用于让 {@link #leaveApplicableWorld()} 只撤销一次。 */
    private boolean fogEffectApplied = false;

    /**
     * 适用维度里调用：喂入一次天气采样，推进状态机。
     *
     * @param fogNow 服务端同步下来的「现在是否雾天」
     * @return 本次需要执行的效果
     */
    public Effect sampleWeather(boolean fogNow) {
        stateBefore = stateNow;
        stateNow = fogNow ? State.FOGGY : State.SUNNY;

        // 穷举四条转移：只有跨越边界的两条需要动作。
        if (stateBefore == State.SUNNY && stateNow == State.FOGGY) {
            fogEffectApplied = true;
            return Effect.APPLY_FOG;
        }
        if (stateBefore == State.FOGGY && stateNow == State.SUNNY) {
            fogEffectApplied = false;
            return Effect.CLEAR_FOG;
        }
        // 晴→晴 / 雾→雾：稳态，无动作。
        return Effect.NO_ACTION;
    }

    /**
     * 离开适用维度时调用：回到初始晴天，并在必要时撤销雾天效果。
     *
     * @return 若此前施加过雾天效果则返回 {@link Effect#CLEAR_FOG}（仅一次），否则 {@link Effect#NO_ACTION}
     */
    public Effect leaveApplicableWorld() {
        boolean wasApplied = fogEffectApplied;
        resetToSunny();
        return wasApplied ? Effect.CLEAR_FOG : Effect.NO_ACTION;
    }

    /** 复位为初始晴天态。 */
    public void resetToSunny() {
        stateBefore = State.SUNNY;
        stateNow = State.SUNNY;
        fogEffectApplied = false;
    }

    /** 当前稳定态。 */
    public State currentState() {
        return stateNow;
    }

    /** 当前是否处于雾天。 */
    public boolean isFoggy() {
        return stateNow == State.FOGGY;
    }

    /** 雾天效果当前是否已施加。 */
    public boolean isFogEffectApplied() {
        return fogEffectApplied;
    }

    // ========================================================================
    // 内置自测入口
    // ========================================================================

    /**
     * 直接在类里跑的自测入口，穷举状态机的常见路径与边缘场景。
     *
     * <p><b>必须带 {@code -ea} 运行</b>（enable assertions）。断言在 JVM 里默认是
     * <em>关闭</em>的，不带 {@code -ea} 时 {@code assert} 语句会被整个跳过，
     * 本方法会「假装全部通过」——那才是真的危险。所以下面第一件事就是自检断言开关。</p>
     *
     * <pre>
     *   javac ... （正常编译）
     *   java -ea com.equilibrium.server_and_client.fog_weather_event.FogWeatherStateMachine
     * </pre>
     *
     * <p>IDE 里同样可以跑，但要在运行配置中勾选 "Enable assertions"（IntelliJ 对应 VM options 加 {@code -ea}）。</p>
     */
    public static void main(String[] args) {
        if (!FogWeatherStateMachine.class.desiredAssertionStatus()) {
            System.err.println("警告：断言未启用！请用 `java -ea ...` 运行，否则本次自测毫无意义。");
        }

        commonPaths();
        edgeCases();

        System.out.println("FogWeatherStateMachine all stage clear");
    }

    /** 常见路径：初始态、四条转移、完整闭环。 */
    private static void commonPaths() {
        FogWeatherStateMachine m = new FogWeatherStateMachine();

        // 初始态必须是「初始晴天」
        assert m.currentState() == State.SUNNY : "初始态应为 SUNNY";
        assert !m.isFoggy() : "初始不应为雾天";
        assert !m.isFogEffectApplied() : "初始不应标记为已施加雾天效果";

        // 晴 → 晴：稳态，无动作
        assert m.sampleWeather(false) == Effect.NO_ACTION : "晴→晴 应为 NO_ACTION";

        // 晴 → 雾：起雾
        assert m.sampleWeather(true) == Effect.APPLY_FOG : "晴→雾 应为 APPLY_FOG";
        assert m.isFoggy() : "进入雾天后 isFoggy 应为 true";
        assert m.isFogEffectApplied() : "进入雾天后应标记已施加";

        // 雾 → 雾：稳态，不重掷
        assert m.sampleWeather(true) == Effect.NO_ACTION : "雾→雾 应为 NO_ACTION";
        assert m.isFoggy() && m.isFogEffectApplied() : "持续雾天时应保持雾态与施加标记";

        // 雾 → 晴：散雾
        assert m.sampleWeather(false) == Effect.CLEAR_FOG : "雾→晴 应为 CLEAR_FOG";
        assert !m.isFoggy() && !m.isFogEffectApplied() : "散雾后应清除雾态与施加标记";

        // 完整闭环：晴 → 雾 → 雾 → 晴
        FogWeatherStateMachine loop = new FogWeatherStateMachine();
        assert loop.sampleWeather(true) == Effect.APPLY_FOG : "闭环 step1 应为 APPLY_FOG";
        assert loop.sampleWeather(true) == Effect.NO_ACTION : "闭环 step2 应为 NO_ACTION";
        assert loop.sampleWeather(false) == Effect.CLEAR_FOG : "闭环 step3 应为 CLEAR_FOG";
    }

    /** 边缘场景：离开维度幂等、维度往返、连续翻转、reset。 */
    private static void edgeCases() {
        // 从未离开过适用维度（也没施加过）→ NO_ACTION，且仍为初始晴天
        FogWeatherStateMachine fresh = new FogWeatherStateMachine();
        assert fresh.leaveApplicableWorld() == Effect.NO_ACTION : "未施加时离开应为 NO_ACTION";
        assert fresh.currentState() == State.SUNNY : "离开后应回到晴天";

        // 已施加 → 只撤销一次，之后必须幂等
        FogWeatherStateMachine applied = new FogWeatherStateMachine();
        applied.sampleWeather(true);
        assert applied.isFogEffectApplied() : "起雾后应为已施加";
        assert applied.leaveApplicableWorld() == Effect.CLEAR_FOG : "已施加时离开应为 CLEAR_FOG";
        assert applied.leaveApplicableWorld() == Effect.NO_ACTION : "离开第二次必须为 NO_ACTION（幂等）";
        assert applied.leaveApplicableWorld() == Effect.NO_ACTION : "离开第三次仍应为 NO_ACTION";
        assert applied.currentState() == State.SUNNY && !applied.isFogEffectApplied() : "离开后应清空全部状态";

        // 维度往返①：主世界起雾 → 进下界(leave) → 回主世界且雾仍开 → 重新压视距
        FogWeatherStateMachine rearm = new FogWeatherStateMachine();
        assert rearm.sampleWeather(true) == Effect.APPLY_FOG : "主世界起雾应为 APPLY_FOG";
        assert rearm.leaveApplicableWorld() == Effect.CLEAR_FOG : "进下界应撤销一次";
        assert rearm.sampleWeather(true) == Effect.APPLY_FOG : "回主世界且雾仍开，应重新压视距（复位重新武装边沿）";

        // 维度往返②：主世界起雾 → 进下界 → 雾已散 → 回主世界不应误压
        FogWeatherStateMachine cleared = new FogWeatherStateMachine();
        cleared.sampleWeather(true);
        cleared.leaveApplicableWorld();
        assert cleared.sampleWeather(false) == Effect.NO_ACTION : "回主世界且雾已散，不应有动作";

        // 相邻采样点连续翻转
        FogWeatherStateMachine flip = new FogWeatherStateMachine();
        assert flip.sampleWeather(false) == Effect.NO_ACTION : "翻转序列 step1";
        assert flip.sampleWeather(true) == Effect.APPLY_FOG : "翻转序列 step2";
        assert flip.sampleWeather(false) == Effect.CLEAR_FOG : "翻转序列 step3";
        assert flip.sampleWeather(true) == Effect.APPLY_FOG : "翻转序列 step4";

        // 状态与 getter 必须自洽
        FogWeatherStateMachine consistent = new FogWeatherStateMachine();
        consistent.sampleWeather(true);
        assert (consistent.currentState() == State.FOGGY) == consistent.isFoggy() : "currentState 与 isFoggy 必须一致";
        assert consistent.isFogEffectApplied() : "雾天态应同时为已施加";

        // resetToSunny 显式复位
        FogWeatherStateMachine r = new FogWeatherStateMachine();
        r.sampleWeather(true);
        r.resetToSunny();
        assert r.currentState() == State.SUNNY : "resetToSunny 后应为晴天";
        assert !r.isFoggy() && !r.isFogEffectApplied() : "resetToSunny 应清空雾态与施加标记";
    }
}
