package com.equilibrium.server_and_client.server.moonphase_tasks;

public class MoonlightController {

    // 定义时间范围常量
    private static final long START_INCREASE = 12769; // 开始线性增加的时间
    private static final long END_INCREASE = 13702;   // 结束线性增加的时间
    private static final long START_DECREASE = 22000; // 开始线性减少的时间
    private static final long END_DECREASE = 22812;   // 结束线性减少的时间

    /**
     * 计算当前时间对应的因子值
     * @param totalTime 总时间（long类型）
     * @return 因子值（float类型）
     */
    public static float calculateFactor(long totalTime) {
        // 计算当前天的时间（0-23999）
        long timeOfDay = totalTime % 24000;

        // 根据当前时间范围计算因子值
        if (timeOfDay >= START_INCREASE && timeOfDay <= END_INCREASE) {
            // 在线性增加时间段，因子值从0增加到1
            return (float) (timeOfDay - START_INCREASE) / (END_INCREASE - START_INCREASE);
        } else if (timeOfDay > END_INCREASE && timeOfDay < START_DECREASE) {
            // 在常量时间段，因子值为1
            return 1.0f;
        } else if (timeOfDay >= START_DECREASE && timeOfDay <= END_DECREASE) {
            // 在线性减少时间段，因子值从1减少到0
            return 1.0f - (float) (timeOfDay - START_DECREASE) / (END_DECREASE - START_DECREASE);
        } else {
            // 在其他时间段，因子值为0
            return 0.0f;
        }
    }
}
