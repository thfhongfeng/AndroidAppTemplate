package com.pine.tool.util;

import android.content.Context;
import android.content.res.TypedArray;

import androidx.annotation.ArrayRes;
import androidx.annotation.IdRes;

public class ResourceUtils {
    @IdRes
    public static int[] getResIdArray(Context context, @ArrayRes int arrResId) {
        TypedArray typedArray = context.getResources().obtainTypedArray(arrResId);
        int len = typedArray.length();
        int[] resIds = new int[len];
        for (int i = 0; i < len; i++) {
            resIds[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();
        return resIds;
    }
}
