package com.equilibrium.server_and_client.server.persistent_state;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MapNbtSerializer {

    /**
     * 将任意Map转换为NBT
     * @param map 要转换的Map
     * @param keyWriter 键的写入函数，需要处理DataOutput
     * @param valueWriter 值的写入函数，需要处理DataOutput
     * @return 包含Map数据的NBT
     */
    public static <K, V> NbtCompound toNbt(
            Map<K, V> map,
            BiConsumer<DataOutput, K> keyWriter,
            BiConsumer<DataOutput, V> valueWriter
    ) {
        NbtCompound nbt = new NbtCompound();

        if (map == null || map.isEmpty()) {
            nbt.putByteArray("data", new byte[]{0});
            return nbt;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(baos)) {

            // 写入条目数量
            dos.writeInt(map.size());

            // 写入每个条目
            for (Map.Entry<K, V> entry : map.entrySet()) {
                keyWriter.accept(dos, entry.getKey());
                valueWriter.accept(dos, entry.getValue());
            }

            dos.flush();
            byte[] data = baos.toByteArray();
            nbt.putByteArray("data", data);

        } catch (IOException e) {
            // 内存操作，理论上不会发生
            throw new RuntimeException("Failed to serialize map to NBT", e);
        }

        return nbt;
    }

    /**
     * 将NBT转换为任意Map
     * @param nbt 包含Map数据的NBT
     * @param keyReader 键的读取函数，需要处理DataInput
     * @param valueReader 值的读取函数，需要处理DataInput
     * @param mapSupplier Map工厂函数，创建空Map
     * @return 反序列化的Map
     */
    public static <K, V> Map<K, V> fromNbt(
            NbtCompound nbt,
            Function<DataInput, K> keyReader,
            Function<DataInput, V> valueReader,
            Supplier<Map<K, V>> mapSupplier
    ) {
        if(nbt==null)
            //第一次加载世界时会程序会秩序在这里
            return mapSupplier.get();
        if (!nbt.contains("data", NbtElement.BYTE_ARRAY_TYPE)) {
            return mapSupplier.get();
        }

        byte[] data = nbt.getByteArray("data");
        if (data.length <= 1) { // 只有0或空数组
            return mapSupplier.get();
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             DataInputStream dis = new DataInputStream(bais)) {

            int count = dis.readInt();
            Map<K, V> map = mapSupplier.get();

            for (int i = 0; i < count; i++) {
                K key = keyReader.apply(dis);
                V value = valueReader.apply(dis);
                map.put(key, value);
            }

            return map;

        } catch (IOException e) {
            // 数据损坏，返回空Map
            return mapSupplier.get();
        }
    }
    // 示例1: ConcurrentHashMap<BlockPos, Integer>
    public static void example1() {
        // 创建Map
        ConcurrentHashMap<BlockPos, Integer> pollutionMap = new ConcurrentHashMap<>();
        pollutionMap.put(new BlockPos(100, 64, 200), 3);
        pollutionMap.put(new BlockPos(101, 64, 201), 5);

        // Map -> NBT
        NbtCompound nbt = MapNbtSerializer.toNbt(
                pollutionMap,
                // 键写入函数
                (dos, pos) -> {
                    try {
                        dos.writeInt(pos.getX());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        dos.writeInt(pos.getY());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        dos.writeInt(pos.getZ());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                // 值写入函数
                (dos, value) -> {
                    try {
                        dos.writeInt(value);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        System.out.println("Map序列化为NBT，大小: " + nbt.getByteArray("data").length + " 字节");

        // NBT -> Map
        ConcurrentHashMap<BlockPos, Integer> restoredMap = (ConcurrentHashMap<BlockPos, Integer>) MapNbtSerializer.fromNbt(
                nbt,
                // 键读取函数
                dis -> {
                    try {
                        return new BlockPos(dis.readInt(), dis.readInt(), dis.readInt());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                // 值读取函数
                dis -> {
                    try {
                        return dis.readInt();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                // Map工厂
                ConcurrentHashMap::new
        );

        System.out.println("NBT反序列化为Map，条目数: " + restoredMap.size());
    }

    // 示例2: HashMap<String, String>
    public static void example2() {
        HashMap<String, String> stringMap = new HashMap<>();
        stringMap.put("key1", "value1");
        stringMap.put("key2", "value2");
        stringMap.put("中文键", "中文值");

        NbtCompound nbt = MapNbtSerializer.toNbt(
                stringMap,
                // 键写入函数
                (dos, key) -> {
                    try {
                        dos.writeUTF(key);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                // 值写入函数
                (dos, value) -> {
                    try {
                        dos.writeUTF(value);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        HashMap<String, String> restoredMap = (HashMap<String, String>) MapNbtSerializer.fromNbt(
                nbt,
                dis -> {
                    try {
                        return dis.readUTF();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                dis -> {
                    try {
                        return dis.readUTF();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                HashMap::new
        );

        System.out.println("String Map: " + restoredMap);
    }

    // 示例3: HashMap<Integer, Double>
    public static void example3() {
        HashMap<Integer, Double> intDoubleMap = new HashMap<>();
        intDoubleMap.put(1, 3.14);
        intDoubleMap.put(2, 2.71);
        intDoubleMap.put(3, 1.618);

        NbtCompound nbt = MapNbtSerializer.toNbt(
                intDoubleMap,
                (dos, key) -> {
                    try {
                        dos.writeInt(key);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                (dos, value) -> {
                    try {
                        dos.writeDouble(value);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        HashMap<Integer, Double> restoredMap = (HashMap<Integer, Double>) MapNbtSerializer.fromNbt(
                nbt,
                dis -> {
                    try {
                        return dis.readInt();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                dis -> {
                    try {
                        return dis.readDouble();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                HashMap::new
        );

        System.out.println("Integer-Double Map: " + restoredMap);
    }

    public static void main(String[] args) {
        // 方法1：设置控制台编码
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // 如果UTF-8不支持，尝试使用平台默认编码
            System.err.println("警告: UTF-8编码不可用，使用默认编码");
        }

        // 方法2：使用PrintWriter包装System.out
        PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out, java.nio.charset.StandardCharsets.UTF_8), true);

        out.println("=== 测试1: BlockPos -> Integer ===");
        testBlockPosIntegerMap(out);

        out.println("\n=== 测试2: String -> String ===");
        testStringStringMap(out);

        out.println("\n=== 测试3: Integer -> Double ===");
        testIntegerDoubleMap(out);

        out.println("\n=== 测试4: UUID -> Boolean ===");
        testUUIDBooleanMap(out);
    }

    private static void testBlockPosIntegerMap(PrintWriter out) {
        ConcurrentHashMap<BlockPos, Integer> pollutionMap = new ConcurrentHashMap<>();
        pollutionMap.put(new BlockPos(100, 64, 200), 3);
        pollutionMap.put(new BlockPos(101, 64, 201), 5);
        pollutionMap.put(new BlockPos(-100, -64, -200), 7);

        out.println("原始Map: " + pollutionMap);

        NbtCompound nbt = MapNbtSerializer.toNbt(
                pollutionMap,
                (dos, pos) -> {
                    try {
                        dos.writeInt(pos.getX());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        dos.writeInt(pos.getY());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        dos.writeInt(pos.getZ());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                (dos, value) -> {
                    try {
                        dos.writeInt(value);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        out.println("NBT数据大小: " + nbt.getByteArray("data").length + " 字节");

        ConcurrentHashMap<BlockPos, Integer> restoredMap = (ConcurrentHashMap<BlockPos, Integer>) MapNbtSerializer.fromNbt(
                nbt,
                dis -> {
                    try {
                        return new BlockPos(dis.readInt(), dis.readInt(), dis.readInt());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                dis -> {
                    try {
                        return dis.readInt();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                ConcurrentHashMap::new
        );

        out.println("还原Map: " + restoredMap);
        out.println("验证结果: " + (pollutionMap.equals(restoredMap) ? " 成功" : "失败"));
    }

    private static void testStringStringMap(PrintWriter out) {
        HashMap<String, String> configMap = new HashMap<>();
        configMap.put("server_name", "我的服务器");
        configMap.put("max_players", "100");
        configMap.put("difficulty", "hard");

        out.println("原始Map: " + configMap);

        NbtCompound nbt = MapNbtSerializer.toNbt(
                configMap,
                (dos, key) -> {
                    try {
                        dos.writeUTF(key);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                (dos, value) -> {
                    try {
                        dos.writeUTF(value);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        HashMap<String, String> restoredMap = (HashMap<String, String>) MapNbtSerializer.fromNbt(
                nbt,
                dis -> {
                    try {
                        return dis.readUTF();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                dis -> {
                    try {
                        return dis.readUTF();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                HashMap::new
        );

        out.println("还原Map: " + restoredMap);
        out.println("验证结果: " + (configMap.equals(restoredMap) ? "✓ 成功" : "✗ 失败"));
    }

    private static void testIntegerDoubleMap(PrintWriter out) {
        HashMap<Integer, Double> scoreMap = new HashMap<>();
        scoreMap.put(1, 95.5);
        scoreMap.put(2, 87.0);
        scoreMap.put(3, 92.75);

        out.println("原始Map: " + scoreMap);

        NbtCompound nbt = MapNbtSerializer.toNbt(
                scoreMap,
                (dos, key) -> {
                    try {
                        dos.writeInt(key);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                (dos, value) -> {
                    try {
                        dos.writeDouble(value);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        HashMap<Integer, Double> restoredMap = (HashMap<Integer, Double>) MapNbtSerializer.fromNbt(
                nbt,
                dis -> {
                    try {
                        return dis.readInt();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                dis -> {
                    try {
                        return dis.readDouble();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                HashMap::new
        );

        out.println("还原Map: " + restoredMap);
        out.println("验证结果: " + (scoreMap.equals(restoredMap) ? "✓ 成功" : "✗ 失败"));
    }

    private static void testUUIDBooleanMap(PrintWriter out) {
        HashMap<String, Boolean> playerStatusMap = new HashMap<>();
        playerStatusMap.put("玩家A", true);
        playerStatusMap.put("玩家B", false);
        playerStatusMap.put("玩家C", true);

        out.println("原始Map: " + playerStatusMap);

        NbtCompound nbt = MapNbtSerializer.toNbt(
                playerStatusMap,
                (dos, key) -> {
                    try {
                        dos.writeUTF(key);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                (dos, value) -> {
                    try {
                        dos.writeBoolean(value);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        HashMap<String, Boolean> restoredMap = (HashMap<String, Boolean>) MapNbtSerializer.fromNbt(
                nbt,
                dis -> {
                    try {
                        return dis.readUTF();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                dis -> {
                    try {
                        return dis.readBoolean();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                HashMap::new
        );

        out.println("还原Map: " + restoredMap);
        out.println("验证结果: " + (playerStatusMap.equals(restoredMap) ? "✓ 成功" : "✗ 失败"));
    }


}