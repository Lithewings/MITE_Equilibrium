package com.equilibrium.util;

public class AnvilPhase {
    public static int getPhaseFromDurability(int maxDurability, int durability) {
        // 最大耐久必须大于0，否则无法划分阶段
        // 确保耐久度在 [0, maxDurability] 范围内
        int clamped = Math.max(0, Math.min(durability, maxDurability));

        // 计算两个分割点
        int oneThird = maxDurability / 3;           // 下1/3边界
        int twoThird = maxDurability * 2 / 3;       // 上2/3边界

        if (clamped > twoThird) {
            return 0;   // 高耐久
        } else if (clamped > oneThird) {
            return 1;   // 中耐久
        } else {
            return 2;   // 低耐久
        }
    }
}
