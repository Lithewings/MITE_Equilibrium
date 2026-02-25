package com.equilibrium.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public final class BooleanStorageUtil {



    // AES 密钥
    private static final String SECRET_KEY = "oU6BC0kCAwEAAQ=="; // 128-bit key
    private static final String ALGORITHM = "AES";

    // 私有构造器防止实例化
    private BooleanStorageUtil() {
        throw new AssertionError("工具类不允许实例化");
    }


    // 内部数据类（私有静态内部类）
    private static class BooleanData implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private final boolean value;

        BooleanData(boolean value) {
            this.value = value;
        }

        boolean getValue() {
            return value;
        }
    }



    /**
     * 保存布尔值到指定路径
     * @param dragonIsDead 要存储的值
     * @param filePath 自定义文件路径
     * @throws IOException 如果发生I/O错误
     */
    // 加密保存方法
    public static void save(boolean dragonIsDead, String filePath) throws IOException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec key = generateKey();
            cipher.init(Cipher.ENCRYPT_MODE, key);

            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(byteOut)) {
                oos.writeObject(new BooleanData(dragonIsDead));
            }

            byte[] encryptedData = cipher.doFinal(byteOut.toByteArray());

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(encryptedData);
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                 | InvalidKeyException | IllegalBlockSizeException
                 | BadPaddingException e) {
            throw new IOException("加密失败: " + e.getMessage(), e);
        }
    }






    private static SecretKeySpec generateKey()
            throws NoSuchAlgorithmException {
        byte[] key = SECRET_KEY.getBytes();
        return new SecretKeySpec(key, ALGORITHM);
    }







    /**
     * 从指定路径加载布尔值
     * @param filePath 文件路径
     * @param defaultValue 文件不存在时的默认值
     * @return 存储值或默认值
     */
    // 解密加载方法
    public static boolean load(String filePath, boolean defaultValue) {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return defaultValue;
        }

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec key = generateKey();
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] encryptedData = Files.readAllBytes(path);
            byte[] decryptedData = cipher.doFinal(encryptedData);

            try (ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(decryptedData))) {
                Object obj = ois.readObject();
                if (obj instanceof BooleanData) {
                    return ((BooleanData) obj).getValue();
                }
            }
        } catch (Exception e) {
            handleException(e);
        }
        return defaultValue;
    }







    private static void handleException(Exception e) {
        // 实际项目中可以接入日志系统
        System.err.println("BooleanStorage操作异常: " + e.getMessage());
        e.printStackTrace();
    }
}