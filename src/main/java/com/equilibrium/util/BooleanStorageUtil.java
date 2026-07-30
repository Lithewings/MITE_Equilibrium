package com.equilibrium.util;

import net.minecraft.SharedConstants;

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
    private static final String SECRET_KEY = "oU68C0kCAwEAAQ=="; // 128-bit key
    private static final String ALGORITHM = "AES";

    public static final String WORLD_INFORMATION_RECORDER = "WorldInformationRecorder.dat";

    public static final String FINISH_GAME_ONCE = "FinishGameOnce.dat";

    // 私有构造器防止实例化
    private BooleanStorageUtil() {
        throw new AssertionError("工具类不允许实例化");
    }


    // 内部数据类（私有静态内部类）
    private static class BooleanData implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private final boolean dragonDead;

        BooleanData(boolean value) {
            this.dragonDead = value;
        }

        boolean getIfDragonIsDead() {
            return dragonDead;
        }

    }

    // 内部数据类（私有静态内部类）
    public static class WorldInformationRecorder implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private final int finishDay;
        private final long seed;
        private final boolean isGrandStageClear;
        private final String version = SharedConstants.CURRENT_VERSION.getName();

        WorldInformationRecorder(int finishDay, long seed, boolean isGrandStageClear) {
            this.finishDay = finishDay;
            this.seed = seed;
            this.isGrandStageClear = isGrandStageClear;
        }

        public int getFinishDay() {
            return finishDay;
        }
        public long getSeed() {
            return seed;
        }
        public String getVersion() {
            return version;
        }
        public boolean getIsGrandStageClear() {
            return isGrandStageClear;
        }
    }

    /**
     * 保存布尔值到指定路径
     *
     * @param dragonIsDead 要存储的值
     * @param filePath     自定义文件路径
     * @throws IOException 如果发生I/O错误
     */
    // 加密保存方法
    public static void saveFinishGameOnce(boolean dragonIsDead, String filePath) throws IOException {
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



    public static void saveWorldInformation(int day, long seed ,boolean isGrandStageClear, String filePath) throws IOException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec key = generateKey();
            cipher.init(Cipher.ENCRYPT_MODE, key);

            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(byteOut)) {
                oos.writeObject(new WorldInformationRecorder(day,seed,isGrandStageClear ));
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
     * @return 存储值或默认值
     */
    // 解密加载方法
    public static boolean loadFinishGameOnce(String filePath) {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return false;
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
                    return ((BooleanData) obj).getIfDragonIsDead();
                }
            }
        } catch (Exception e) {
            handleException(e);
        }
        return false;
    }



    public static WorldInformationRecorder loadWorldInformation(String filePath) {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return null;
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
                if (obj instanceof WorldInformationRecorder) {
                    return ((WorldInformationRecorder) obj);
                }
            }
        } catch (Exception e) {
            handleException(e);
        }
        return null;
    }




    private static void handleException(Exception e) {
        // 实际项目中可以接入日志系统
        System.err.println("BooleanStorage操作异常: " + e.getMessage());
        e.printStackTrace();
    }
}