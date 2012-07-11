package org.phw.core.lang;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.phw.core.exception.AppException;

/**
 * 编码处理。
 * @author BingooHuang
 *
 */
public class Codec {
    /**
     * UTF-8编码名称。
     */
    public static final String UTF_8 = "UTF-8";

    /** 
     * MAC算法可选以下多种算法 。
     *  
     * <pre> 
     * HmacMD5  
     * HmacSHA1  
     * HmacSHA256  
     * HmacSHA384  
     * HmacSHA512 
     * </pre> 
     */
    private static final String KEY_MAC = "HmacMD5";

    /** 
     * 初始化HMAC密钥。
     *  
     * @return  HMAC密钥
     */
    public static String initMacKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_MAC);
            SecretKey secretKey = keyGenerator.generateKey();
            return toBase64(secretKey.getEncoded());
        }
        catch (NoSuchAlgorithmException e) {
            throw new AppException(e);
        }
    }

    /** 
     * HMAC加密 。
     *  
     * @param data  数据
     * @param key 密钥
     * @return 加密数据
     */
    public static String toHMAC(String data, String key) {
        SecretKey secretKey = new SecretKeySpec(Codec.fromBase64(key), KEY_MAC);
        try {
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);

            byte[] ret = mac.doFinal(data.getBytes(UTF_8));
            return Codec.toBase64(ret);
        }
        catch (UnsupportedEncodingException e) {
            throw new AppException(e);
        }
        catch (NoSuchAlgorithmException e) {
            throw new AppException(e);
        }
        catch (InvalidKeyException e) {
            throw new AppException(e);
        }
    }

    /**
     * SHA-1 消息摘要（校验和）.
     * 固定输出28个字节长的字符串.
     * @param s 字符串
     * @return SHA-1消息摘要
     */
    public static String toSHA1(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(s.getBytes(UTF_8));
            return Codec.toBase64(digest);
        }
        catch (UnsupportedEncodingException e) {
            throw new AppException(e);
        }
        catch (NoSuchAlgorithmException e) {
            throw new AppException(e);
        }
    }

    /**
     * SHA-1 消息摘要。
     * @param str 字符串
     * @param salt 盐
     * @return 经过3次的加盐的哈希值
     */
    public static String toSHA1(String str, String salt) {
        try {
            return toSHA1(3, str, salt.getBytes(UTF_8));
        }
        catch (UnsupportedEncodingException e) {
            throw new AppException(e);
        }
    }

    /**
     * SHA-1 消息摘要。
     * @param iterationNb 循环次数
     * @param str 字符串
     * @param salt 盐
     * @return 经过指定循环次数的加盐的哈希值
     */
    public static String toSHA1(int iterationNb, String str, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(salt);
            byte[] input = digest.digest(str.getBytes(UTF_8));
            for (int i = 0; i < iterationNb; i++) {
                digest.reset();
                input = digest.digest(input);
            }
            return Codec.toBase64(input);
        }
        catch (UnsupportedEncodingException e) {
            throw new AppException(e);
        }
        catch (NoSuchAlgorithmException e) {
            throw new AppException(e);
        }
    }

    /**
     * MD5消息摘要。
     * @param s 字符串。
     * @return MD5摘要。
     */
    public static String toMD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(s.getBytes(UTF_8));
            return Codec.toBase64(digest);
        }
        catch (UnsupportedEncodingException e) {
            throw new AppException(e);
        }
        catch (NoSuchAlgorithmException e) {
            throw new AppException(e);
        }
    }

    /**
     * 将字节数组转换成BASE64字符串。
     * @param array 字节数组。
     * @return BASE64字符串。
     */
    public static String toBase64(byte[] array) {
        return DatatypeConverter.printBase64Binary(array);
    }

    /**
     * 将Base64字符串转换成字节数组。
     * @param s Base64字符串。
     * @return 字节数组。
     */
    public static byte[] fromBase64(String s) {
        return DatatypeConverter.parseBase64Binary(s);
    }

    /**
     * 将字节数组转换成16进制字符串。
     * @param array 字节数组。
     * @return 16进制字符串。
     */
    public static String toHex(byte[] array) {
        return DatatypeConverter.printHexBinary(array);
    }

    /**
     * 将16进制字符串转换成字节数组。
     * @param s 16进制字符串。
     * @return 字节数组。
     */
    public static byte[] fromHex(String s) {
        return DatatypeConverter.parseHexBinary(s);
    }

    /**
     * 将16进制字符串转换成字节数组。
     * @param s 16进制字符串。
     * @return 字节数组。
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
