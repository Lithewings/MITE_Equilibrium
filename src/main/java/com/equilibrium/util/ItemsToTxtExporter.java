package com.equilibrium.util;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ItemsToTxtExporter {
    private static final int DEFAULT_VALUE = 32;

    /**
     * 主方法：导出所有Item到txt文件
     */
    public static void exportAllItemsToTxt() {
        System.out.println("开始导出Items类所有成员变量到txt文件...");

        try {
            // 收集所有Item字段
            List<ItemFieldInfo> itemFields = collectAllItemFields();

            // 排序（按字段名）
            itemFields.sort(Comparator.comparing(ItemFieldInfo::getFieldName));

            // 输出到文件
            String output = generatePutStatements(itemFields);

            // 保存到文件
            String fileName = saveToFile(output);

            System.out.println("成功导出 " + itemFields.size() + " 个Item到文件: " + fileName);
            System.out.println("文件内容预览（前10行）:");
            System.out.println(output.lines().limit(10).collect(Collectors.joining("\n")));

        } catch (Exception e) {
            System.err.println("导出失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Item字段信息类
     */
    private static class ItemFieldInfo {
        private final String fieldName;
        private final Item item;
        private final String registryId;

        public ItemFieldInfo(String fieldName, Item item) {
            this.fieldName = fieldName;
            this.item = item;
            this.registryId = Registries.ITEM.getId(item).toString();
        }

        public String getFieldName() {
            return fieldName;
        }

        public Item getItem() {
            return item;
        }

        public String getRegistryId() {
            return registryId;
        }
    }

    /**
     * 收集Items类中的所有Item字段
     */
    private static List<ItemFieldInfo> collectAllItemFields() {
        List<ItemFieldInfo> itemFields = new ArrayList<>();

        try {
            // 获取Items类
            Class<?> itemsClass = Items.class;

            // 获取所有字段
            Field[] fields = itemsClass.getDeclaredFields();

            for (Field field : fields) {
                if (isValidItemField(field)) {
                    try {
                        // 获取字段值（Item实例）
                        Item item = (Item) field.get(null);
                        if (item != null) {
                            itemFields.add(new ItemFieldInfo(field.getName(), item));
                        }
                    } catch (IllegalAccessException e) {
                        System.err.println("无法访问字段 " + field.getName() + ": " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("收集Item字段时出错: " + e.getMessage());
        }

        return itemFields;
    }

    /**
     * 检查字段是否为有效的Item字段
     */
    private static boolean isValidItemField(Field field) {
        int modifiers = field.getModifiers();

        // 检查是否为public static final Item
        if (!Modifier.isPublic(modifiers)) return false;
        if (!Modifier.isStatic(modifiers)) return false;
        if (!Modifier.isFinal(modifiers)) return false;
        if (!Item.class.isAssignableFrom(field.getType())) return false;

        return true;
    }

    /**
     * 生成put语句
     */
    private static String generatePutStatements(List<ItemFieldInfo> itemFields) {
        StringBuilder sb = new StringBuilder();

        // 添加文件头
        sb.append("// Items类所有Item成员变量导出\n");
        sb.append("// 生成时间: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n");
        sb.append("// 总数量: ").append(itemFields.size()).append("\n");
        sb.append("// 固定值: ").append(DEFAULT_VALUE).append("\n\n");

        sb.append("// 导入必要的包\n");
        sb.append("import net.minecraft.item.Item;\n");
        sb.append("import net.minecraft.item.Items;\n\n");

        sb.append("// 创建哈希表\n");
        sb.append("Map<Item, Integer> itemMap = new HashMap<>();\n\n");

        sb.append("// 将所有Item添加到哈希表中\n");

        // 生成put语句
        for (ItemFieldInfo info : itemFields) {
            String fieldName = info.getFieldName();
            String registryId = info.getRegistryId();

            // 生成带注释的put语句
            sb.append(String.format("itemMap.put(Items.%s, %d); // %s\n",
                    fieldName, DEFAULT_VALUE, registryId));
        }

        // 添加文件尾
        sb.append("\n// 哈希表使用示例\n");
        sb.append("// int value = itemMap.get(Items.DIAMOND); // 获取值\n");
        sb.append("// boolean contains = itemMap.containsKey(Items.GOLD_INGOT); // 检查是否包含\n");
        sb.append("// int size = itemMap.size(); // 获取大小\n");

        return sb.toString();
    }

    /**
     * 保存内容到文件
     */
    private static String saveToFile(String content) throws IOException {
        // 获取Minecraft运行目录
        Path minecraftDir = Paths.get("").toAbsolutePath();

        // 创建输出目录
        Path outputDir = minecraftDir.resolve("items_output");
        if (!outputDir.toFile().exists()) {
            outputDir.toFile().mkdirs();
        }

        // 生成文件名（带时间戳）
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "items_export_" + timestamp + ".txt";
        Path filePath = outputDir.resolve(fileName);

        // 写入文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
            writer.write(content);
        }

        // 同时生成一个简化的版本
        generateSimplifiedVersion(outputDir, timestamp);

        return filePath.toString();
    }

    /**
     * 生成简化版本（仅put语句）
     */
    private static void generateSimplifiedVersion(Path outputDir, String timestamp) throws IOException {
        List<ItemFieldInfo> itemFields = collectAllItemFields();
        itemFields.sort(Comparator.comparing(ItemFieldInfo::getFieldName));

        Path simplifiedPath = outputDir.resolve("items_simple_" + timestamp + ".txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(simplifiedPath.toFile()))) {
            writer.write("// Items类所有Item成员变量（简化版）\n");
            writer.write("// 生成时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
            writer.write("// 总数量: " + itemFields.size() + "\n\n");

            for (ItemFieldInfo info : itemFields) {
                writer.write(String.format("itemMap.put(Items.%s, 32);\n", info.getFieldName()));
            }
        }
    }

    /**
     * 获取所有Item字段名列表
     */
    public static List<String> getAllItemFieldNames() {
        return collectAllItemFields().stream()
                .map(ItemFieldInfo::getFieldName)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 获取所有Item及其注册ID
     */
    public static Map<String, String> getAllItemsWithIds() {
        return collectAllItemFields().stream()
                .collect(Collectors.toMap(
                        ItemFieldInfo::getFieldName,
                        ItemFieldInfo::getRegistryId,
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }
}