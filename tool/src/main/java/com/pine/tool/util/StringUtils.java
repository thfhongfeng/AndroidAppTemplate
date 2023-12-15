package com.pine.tool.util;

import android.text.TextUtils;

import com.vdurmont.emoji.EmojiParser;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tanghongfeng on 2018/11/13
 */

public class StringUtils {
    public static String toChineseNumber(String numberStr) {
        String[] s1 = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        String[] s2 = {"十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千"};
        String result = "";
        int n = numberStr.length();
        for (int i = 0; i < n; i++) {
            int num = numberStr.charAt(i) - '0';
            if (i != n - 1 && num != 0) {
                result += s1[num] + s2[n - 2 - i];
            } else {
                if (num != 0 || !s1[0].equals(result.charAt(result.length() - 1)) && i != n - 1) {
                    result += s1[num];
                }
            }
        }
        return result;
    }

    public static String toChineseNumber(int number) {
        String numberStr = String.valueOf(number);
        return toChineseNumber(numberStr);
    }

    public static boolean hasEmojis(String str) {
        return !EmojiParser.removeAllEmojis(str).equals(str);
    }

    public static int toInt(String number) {
        if (TextUtils.isEmpty(number)) {
            return 0;
        } else {
            return Integer.parseInt(number);
        }
    }

    public static float toFloat(String number) {
        if (TextUtils.isEmpty(number)) {
            return 0.0f;
        } else {
            return Float.parseFloat(number);
        }
    }

    public static String toPrintNoBlandStr(String str) {
        String dest = "";
        if (!TextUtils.isEmpty(str)) {
            dest = StringUtils.replaceBlank(
                    StringUtils.replaceUnPrint(str));
        }
        return dest;
    }

    /**
     * java去除字符串中的空格、回车、换行符、制表符
     *
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (!TextUtils.isEmpty(str)) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * java去除字符串中的空格、回车、换行符、制表符
     *
     * @param str
     * @return
     */
    public static String replaceUnPrint(String str) {
        String dest = "";
        if (!TextUtils.isEmpty(str)) {
            dest = str.replaceAll("\\P{Print}", "");
        }
        return dest;
    }

    public static String join(String delimiter, String[] elements) {
        if (elements == null || elements.length < 1) {
            return "";
        }
        if (elements.length == 1) {
            return elements[0];
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(elements[0]);
        for (int i = 1; i < elements.length; i++) {
            stringBuilder.append(delimiter).append(elements[i]);
        }
        return stringBuilder.toString();
    }

    public static String join(String delimiter, List<String> elements) {
        if (elements == null || elements.size() < 1) {
            return "";
        }
        if (elements.size() == 1) {
            return elements.get(0);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(elements.get(0));
        for (int i = 1; i < elements.size(); i++) {
            stringBuilder.append(delimiter).append(elements.get(i));
        }
        return stringBuilder.toString();
    }

    public static <T> boolean hasElements(List<T> list) {
        return list != null && list.size() > 0;
    }

    public static boolean hasElements(Object[] list) {
        return list != null && list.length > 0;
    }
}
