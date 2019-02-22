package com.pine.tool.util;

import android.text.TextUtils;

import java.util.Random;

/**
 * Created by tanghongfeng on 2018/11/15
 */

public class RandomUtils {
    public static final String NUMBERS_AND_LETTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String NUMBERS = "0123456789";
    public static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String CAPITAL_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWER_CASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";

    private RandomUtils() {
        throw new AssertionError();
    }

    public static String getRandomNumbersAndLetters(int length) {
        return getRandom(NUMBERS_AND_LETTERS, length);
    }

    public static String getRandomNumbers(int length) {
        return getRandom(NUMBERS, length);
    }

    public static String getRandomLetters(int length) {
        return getRandom(LETTERS, length);
    }

    public static String getRandomCapitalLetters(int length) {
        return getRandom(CAPITAL_LETTERS, length);
    }

    public static String getRandomLowerCaseLetters(int length) {
        return getRandom(LOWER_CASE_LETTERS, length);
    }

    public static String getRandom(String source, int length) {
        return TextUtils.isEmpty(source) ? null : getRandom(source.toCharArray(), length);
    }

    public static String getRandom(char[] sourceChar, int length) {
        if (sourceChar != null && sourceChar.length != 0 && length >= 0) {
            StringBuilder str = new StringBuilder(length);
            Random random = new Random();

            for (int i = 0; i < length; ++i) {
                str.append(sourceChar[random.nextInt(sourceChar.length)]);
            }

            return str.toString();
        } else {
            return null;
        }
    }

    public static int getRandom(int max) {
        return getRandom(0, max);
    }

    public static int getRandom(int min, int max) {
        if (min > max) {
            return 0;
        } else {
            return min == max ? min : min + (new Random()).nextInt(max - min);
        }
    }

    public static boolean shuffle(Object[] objArray) {
        return objArray == null ? false : shuffle(objArray, getRandom(objArray.length));
    }

    public static boolean shuffle(Object[] objArray, int shuffleCount) {
        int length;
        if (objArray != null && shuffleCount >= 0 && (length = objArray.length) >= shuffleCount) {
            for (int i = 1; i <= shuffleCount; ++i) {
                int random = getRandom(length - i);
                Object temp = objArray[length - i];
                objArray[length - i] = objArray[random];
                objArray[random] = temp;
            }
            return true;
        } else {
            return false;
        }
    }

    public static int[] shuffle(int[] intArray) {
        return intArray == null ? null : shuffle(intArray, getRandom(intArray.length));
    }

    public static int[] shuffle(int[] intArray, int shuffleCount) {
        int length;
        if (intArray != null && shuffleCount >= 0 && (length = intArray.length) >= shuffleCount) {
            int[] out = new int[shuffleCount];

            for (int i = 1; i <= shuffleCount; ++i) {
                int random = getRandom(length - i);
                out[i - 1] = intArray[random];
                int temp = intArray[length - i];
                intArray[length - i] = intArray[random];
                intArray[random] = temp;
            }
            return out;
        } else {
            return null;
        }
    }
}
