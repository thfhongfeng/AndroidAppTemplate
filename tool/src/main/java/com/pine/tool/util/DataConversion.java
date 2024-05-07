package com.pine.tool.util;

import android.nfc.FormatException;

import java.util.List;

/**
 * 数据转换工具
 */
public class DataConversion {

    /**
     * 判断奇数或偶数，位运算，最后一位是1则为奇数，为0是偶数
     *
     * @param num
     * @return
     */
    public static int isOdd(int num) {
        return num & 0x1;
    }

    /**
     * 将int转成byte
     *
     * @param number
     * @return
     */
    public static byte intToByte(int number) throws FormatException {
        return hexToByte(intToHex(number));
    }

    /**
     * 将int转成hex字符串
     *
     * @param number
     * @return
     */
    public static String intToHex(int number) {
        String st = Integer.toHexString(number).toUpperCase();
        return String.format("%2s", st).replaceAll(" ", "0");
    }

    /**
     * 字节转十进制
     *
     * @param b
     * @return
     */
    public static int byteToDec(byte b) {
        String s = byteToHex(b);
        return (int) hexToDec(s);
    }

    /**
     * 字节数组转十进制
     *
     * @param bytes
     * @return
     */
    public static int bytesToDec(byte[] bytes) {
        String s = encodeHexString(bytes);
        return (int) hexToDec(s);
    }

    /**
     * Hex字符串转int
     *
     * @param inHex
     * @return
     */
    public static int hexToInt(String inHex) {
        return Integer.parseInt(inHex, 16);
    }

    /**
     * 字节转十六进制字符串
     *
     * @param num
     * @return
     */
    public static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits).toUpperCase();
    }

    /**
     * 十六进制转byte字节
     *
     * @param hexString
     * @return
     */
    public static byte hexToByte(String hexString) throws FormatException {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private static int toDigit(char hexChar) throws FormatException {
        int digit = Character.digit(hexChar, 16);
        if (digit == -1) {
            throw new FormatException(
                    "Invalid Hexadecimal Character: " + hexChar);
        }
        return digit;
    }

    /**
     * 字节数组转十六进制
     *
     * @param byteArray
     * @return
     */
    public static String encodeHexString(List<Byte> byteArray) {
        if (byteArray == null) {
            return "";
        }
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.size(); i++) {
            hexStringBuffer.append(byteToHex(byteArray.get(i)));
        }
        return hexStringBuffer.toString().toUpperCase();
    }


    /**
     * 字节数组转十六进制
     *
     * @param byteArray
     * @return
     */
    public static String encodeHexString(byte[] byteArray) {
        if (byteArray == null) {
            return "";
        }
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        return hexStringBuffer.toString().toUpperCase();
    }

    /**
     * 十六进制转字节数组
     *
     * @param hexString
     * @return
     */
    public static byte[] decodeHexString(String hexString) throws FormatException {
        if (hexString.length() % 2 == 1) {
            throw new FormatException(
                    "Invalid hexadecimal String supplied.");
        }
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    /**
     * 十进制转十六进制
     *
     * @param dec
     * @return
     */
    public static String decToHex(int dec) {
        String hex = Integer.toHexString(dec);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex.toLowerCase();
    }

    /**
     * 十六进制转十进制
     *
     * @param hex
     * @return
     */
    public static long hexToDec(String hex) {
        return Long.parseLong(hex, 16);
    }

    /**
     * 字符串转换unicode字符串
     */
    public static String string2UnicodeStr(String str) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            // 取出每一个字符
            char c = str.charAt(i);
            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }
        return unicode.toString();
    }

    /**
     * unicode字符串转化成为16进制字符串
     *
     * @param unicodeStr
     * @return
     */
    public static String unicodeStrToHexStr(String unicodeStr) {
        String str = "";
        for (int i = 0; i < unicodeStr.length(); i++) {
            int ch = (int) unicodeStr.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    /**
     * 16进制转换成为unicode字符串
     *
     * @param hexStr
     * @return
     */
    public static String hexStrToUnicodeStr(String hexStr) {
        if (hexStr == null || hexStr.equals("")) {
            return null;
        }
        hexStr = hexStr.replace(" ", "");
        byte[] baKeyword = new byte[hexStr.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(hexStr.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            hexStr = new String(baKeyword, "UTF-8");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return hexStr;
    }

    /**
     * unicode字符串转字符串
     */
    public static String unicodeStrToString(String unicodeStr) {
        StringBuffer string = new StringBuffer();
        String[] hex = unicodeStr.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);
            // 追加成string
            string.append((char) data);
        }
        return string.toString();
    }

    /**
     * 字符串转换成为16进制字符串(无需Unicode编码)
     *
     * @param str
     * @return
     */
    public static String stringToHexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * 16进制字符串直接转换成为字符串(无需Unicode解码)
     *
     * @param hexStr
     * @return
     */
    public static String hexStrToString(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }
}