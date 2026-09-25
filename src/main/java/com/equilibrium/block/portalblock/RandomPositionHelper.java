package com.equilibrium.block.portalblock;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

public class RandomPositionHelper {
    /**
     * 在中心点周围的正方形环形区域（4000 < max(|dx|,|dz|) <= 8000）内随机选取一个坐标。
     *
     * @param center 中心坐标
     * @param random 随机数生成器（Minecraft 的 Random 实例）
     * @param seaLevel 如果不指定,则以生物所在y值确定最终传送高度
     * @return 新的 BlockPos，仅 x 和 z 改变，y始终为海平面高度
     */
    public static BlockPos getRandomPosInSquareRing(BlockPos center, RandomSource random , @Nullable Integer seaLevel) {
        int dx, dz;
        do {
            // 生成 [-8000, 8000] 范围内的随机偏移
            dx = random.nextInt(16001) - 8000;
            dz = random.nextInt(16001) - 8000;
        } while (Math.max(Math.abs(dx), Math.abs(dz)) <= 4000);

        return new BlockPos(center.getX() + dx, seaLevel==null? center.getY():seaLevel , center.getZ() + dz);
    }

    /**
     * 测试方法：打印若干随机生成的坐标，并显示其切比雪夫距离。
     */
    public static void main(String[] args) {
        BlockPos center = new BlockPos(0, 64, 0);
        RandomSource random = RandomSource.create(); // 使用 Minecraft 提供的随机数生成器

        System.out.println("Center Position: " + center.toShortString());
        for (int i = 0; i < 10; i++) {
            BlockPos randomPos = getRandomPosInSquareRing(center, random, 144);
            int dx = randomPos.getX() - center.getX();
            int dz = randomPos.getZ() - center.getZ();
            int chebyshevDist = Math.max(Math.abs(dx), Math.abs(dz));
            System.out.println("Pos: " + randomPos.toShortString()
                    + " | offset: (" + dx + ", " + dz + ")"
                    + " | max vertical distance (Chebyshev) : " + chebyshevDist);
        }
    }
}