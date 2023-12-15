package com.pine.tool.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.pine.tool.bean.EncryptData;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class CryptoUtils {
    private static final String DEFAULT_SECRETKEY_NAME = "default_secretkey_name";

    private static final String STORE_FILE_NAME = "crypto";
    private static CryptoUtils instance;

    private KeyStore keyStore;

    private CryptoUtils(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected synchronized static CryptoUtils getInstance(Context context) {
        if (instance == null) {
            File file = new File(context.getFilesDir(), STORE_FILE_NAME);
            try {
                KeyStore keyStore = getKeyStore(file);
                initKey(keyStore, file);
                instance = new CryptoUtils(keyStore);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return instance;
    }

    private static void initKey(KeyStore keyStore, File file) throws Exception {
        if (!keyStore.containsAlias(DEFAULT_SECRETKEY_NAME)) { // 秘钥不存在，则生成秘钥
            KeyGenerator keyGenerator = generateKeyGenerator();
            SecretKey secretKey = keyGenerator.generateKey();
            storeKey(keyStore, file, secretKey);
        }
    }

    private static void storeKey(KeyStore keyStore, File file, SecretKey secretKey) throws Exception {
        if (Build.VERSION.SDK_INT >= 23) {
            keyStore.setKeyEntry(DEFAULT_SECRETKEY_NAME, secretKey, null, null);
        } else {
            keyStore.setKeyEntry(DEFAULT_SECRETKEY_NAME, secretKey, null, null);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                keyStore.store(fos, null);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
        }
    }

    private static KeyStore getKeyStore(File file) throws Exception {
        if (Build.VERSION.SDK_INT >= 23) {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            return keyStore;
        } else {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            if (!file.exists()) {
                boolean isSuccess = file.createNewFile();
                if (!isSuccess) {
                    throw new SecurityException("创建内部存储文件失败");
                }
                keyStore.load(null, null);
            } else if (file.length() <= 0) {
                keyStore.load(null, null);
            } else {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    keyStore.load(fis, null);
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fis != null) {
                        fis.close();
                    }
                }
            }
            return keyStore;
        }
    }

    @SuppressLint("DeletedProvider")
    private static KeyGenerator generateKeyGenerator() throws Exception {
        KeyGenerator keyGenerator;
        if (Build.VERSION.SDK_INT >= 23) {
            keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore");
            keyGenerator.init(new KeyGenParameterSpec.Builder(DEFAULT_SECRETKEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(false)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
        } else {
            keyGenerator = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "Crypto");
            secureRandom.setSeed(generateSeed());
            keyGenerator.init(128, secureRandom);
        }

        return keyGenerator;
    }

    private static byte[] generateSeed() {
        try {
            ByteArrayOutputStream seedBuffer = new ByteArrayOutputStream();
            DataOutputStream seedBufferOut =
                    new DataOutputStream(seedBuffer);
            seedBufferOut.writeLong(System.currentTimeMillis());
            seedBufferOut.writeLong(System.nanoTime());
            seedBufferOut.writeInt(android.os.Process.myPid());
            seedBufferOut.writeInt(android.os.Process.myUid());
            seedBufferOut.write(Build.BOARD.getBytes());
            return seedBuffer.toByteArray();
        } catch (IOException e) {
            throw new SecurityException("Failed to generate seed", e);
        }
    }

    private static SecretKey getSecretKey(KeyStore keyStore) {
        try {
            return (SecretKey) keyStore.getKey(DEFAULT_SECRETKEY_NAME, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    int base64Flag = Base64.NO_WRAP;

    /**
     * AES加密
     *
     * @param content
     * @return
     */
    private EncryptData aesEncrypt(String content) throws Exception {
        try {
            SecretKey secretKey = getSecretKey(keyStore);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher.getIV();
            byte[] bytes = cipher.doFinal(content.getBytes());
            return new EncryptData(Base64.encodeToString(bytes, base64Flag),
                    Base64.encodeToString(iv, base64Flag));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * AES解密
     *
     * @param data
     * @return
     */
    private String aesDecrypt(EncryptData data) throws Exception {
        try {
            SecretKey secretKey = getSecretKey(keyStore);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey,
                    new IvParameterSpec(Base64.decode(data.getIv(), base64Flag)));
            byte[] bytes = cipher.doFinal(Base64.decode(data.getContent(), base64Flag));
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static EncryptData encrypt(Context context, @NonNull String content) throws Exception {
        if (TextUtils.isEmpty(content)) {
            throw new Exception("encrypt content is empty");
        }
        CryptoUtils cryptoUtils = CryptoUtils.getInstance(context);
        if (cryptoUtils != null) {
            return cryptoUtils.aesEncrypt(content);
        }
        throw new Exception("encrypt err");
    }

    public static String decrypt(Context context, EncryptData data) throws Exception {
        if (data == null ||
                TextUtils.isEmpty(data.getContent()) || TextUtils.isEmpty(data.getContent())) {
            throw new Exception("decrypt data is empty");
        }
        CryptoUtils cryptoUtils = CryptoUtils.getInstance(context);
        if (cryptoUtils != null) {
            return cryptoUtils.aesDecrypt(data);
        }
        throw new Exception("decrypt err");
    }

    public static String encryptContent2JsonStr(Context context, String content) {
        try {
            EncryptData encryptData = CryptoUtils.encrypt(context, content);
            return encryptData == null ? null : encryptData.getJsonString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptJsonStr2Content(Context context, String encryptJsonStr) {
        try {
            EncryptData encryptData = EncryptData.fromJsonString(encryptJsonStr);
            return CryptoUtils.decrypt(context, encryptData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
