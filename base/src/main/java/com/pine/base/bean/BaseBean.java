package com.pine.base.bean;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.HashMap;

public class BaseBean {
    public HashMap<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        Class clazz = this.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                if (field.getName().equals("serialVersionUID")) {
                    continue;
                }
                field.setAccessible(true);
                Object obj = field.get(this);
                if (obj != null && obj instanceof BaseBean) {
                    map.putAll(((BaseBean) obj).toMap());
                } else {
                    map.put(field.getName(), obj != null ? String.valueOf(field.get(this)) : "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public HashMap<String, String> toMapIgnoreEmpty() {
        HashMap<String, String> map = new HashMap<>();
        Class clazz = this.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                if (field.getName().equals("serialVersionUID")) {
                    continue;
                }
                field.setAccessible(true);
                Object obj = field.get(this);
                if (obj != null && obj instanceof BaseBean) {
                    map.putAll(((BaseBean) obj).toMapIgnoreEmpty());
                } else if (!isNullObj(obj)) {
                    map.put(field.getName(), String.valueOf(obj));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private boolean isNullObj(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof Integer) {
            if ((int) obj == Integer.MAX_VALUE) {
                return true;
            }
        } else if (obj instanceof Short) {
            if ((short) obj == Short.MAX_VALUE) {
                return true;
            }
        } else if (obj instanceof Long) {
            if ((long) obj == Long.MAX_VALUE) {
                return true;
            }
        } else if (obj instanceof Float) {
            if ((float) obj == Float.MAX_VALUE) {
                return true;
            }
        } else if (obj instanceof Double) {
            if ((double) obj == Double.MAX_VALUE) {
                return true;
            }
        }
        return TextUtils.isEmpty(obj.toString());
    }
}
