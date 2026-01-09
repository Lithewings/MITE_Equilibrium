package com.equilibrium.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class BlockHardnessSorterBootstrap {
    // 返回方块-硬度的哈希表（仅保留名称和硬度）
    public static Map<String, Float> mainMethod() {
        // 存储方块信息的列表
        List<BlockInfo> blockList = new ArrayList<>();

        // 反射获取Blocks类中所有方块字段
        Field[] fields = Blocks.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                // 筛选public static final Block类型的字段
                if (field.getType() == Block.class
                        && java.lang.reflect.Modifier.isPublic(field.getModifiers())
                        && java.lang.reflect.Modifier.isStatic(field.getModifiers())
                        && java.lang.reflect.Modifier.isFinal(field.getModifiers())) {

                    Block block = (Block) field.get(null); // 静态字段无需实例化
                    String blockName = field.getName(); // 方块名称（如STONE、GRASS_BLOCK）
                    float hardness = block.getHardness(); // 获取硬度值

                    blockList.add(new BlockInfo(blockName, hardness));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // 排序：按硬度降序，同硬度按名称排序
        boolean ascending = false;
        if (ascending) {
            blockList.sort(Comparator.comparingDouble(BlockInfo::getHardness)
                    .thenComparing(BlockInfo::getBlockName));
        } else {
            blockList.sort(Comparator.comparingDouble(BlockInfo::getHardness)
                    .thenComparing(BlockInfo::getBlockName)
                    .reversed());
        }

        // 构建方块-硬度的哈希表（保留排序顺序）
        Map<String, Float> blockHardnessMap = new LinkedHashMap<>();
        for (BlockInfo info : blockList) {
            blockHardnessMap.put(info.getBlockName(), info.getHardness());
        }

        // ========== 1. 写入JSON文件（方块-硬度映射，保留排序） ==========
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("block_hardness_sorted.json")) {
            gson.toJson(blockHardnessMap, writer);
            System.out.println("✅ JSON文件生成完成！结果已保存到 block_hardness_sorted.json");
        } catch (IOException e) {
            System.err.println("❌ JSON文件写入失败：" + e.getMessage());
            e.printStackTrace();
        }

        // ========== 2. 生成可直接复制的put()语句（控制台+文本文件） ==========
        // 拼接put语句
        StringBuilder putStatements = new StringBuilder();
        putStatements.append("// 方块硬度哈希表put语句（按硬度降序）\n");
        putStatements.append("// 可直接复制到代码中使用\n");
        for (Map.Entry<String, Float> entry : blockHardnessMap.entrySet()) {
            // 格式化：map.put("方块名", 硬度值); （浮点数值保留2位小数，避免科学计数法）
            putStatements.append(String.format("map.put(\"%s\", %.2f);\n",
                    entry.getKey(), entry.getValue()));
        }

        // 输出到控制台（方便快速复制）
        System.out.println("\n========== 可直接复制的put()语句 ==========\n");
        System.out.println(putStatements);

        // 输出到文本文件（方便批量复制）
        try (FileWriter writer = new FileWriter("block_put_statements.txt")) {
            writer.write(putStatements.toString());
            System.out.println("\n✅ put()语句已保存到 block_put_statements.txt");
        } catch (IOException e) {
            System.err.println("❌ put语句文件写入失败：" + e.getMessage());
            e.printStackTrace();
        }

        // 返回哈希表
        return blockHardnessMap;
    }

    // 简化的方块信息内部类（仅保留名称和硬度）
    static class BlockInfo {
        private final String blockName;
        private final float hardness;

        public BlockInfo(String blockName, float hardness) {
            this.blockName = blockName;
            this.hardness = hardness;
        }

        public String getBlockName() {
            return blockName;
        }

        public float getHardness() {
            return hardness;
        }
    }

    // 测试主方法
    public static void main(String[] args) {
        BlockHardnessSorterBootstrap.mainMethod();
    }
}