package com.pine.tool.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.tool.util.type.ParameterizedTypeImpl;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public class SharePreferenceUtils {
    public static final String TAG = LogUtils.makeLogTag(SharePreferenceUtils.class);

    // 应用缓存SharePreference，生命周期随用户指定
    public static final String CACHE_SP = "cache_share_preference";
    // 应用配置SharePreference，生命周期与APP安装周期相同（APP卸载才会被清理）
    public static final String CONFIG_SP = "config_share_preference";
    // 应用本次启动SharePreference，生命周期为本次APP启动周期（APP每次重新启动时清理）
    public static final String APP_LIVED_CACHE_SP = "app_lived_cache_share_preference";

    private SharePreferenceUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    private static Context getContext() {
        return AppUtils.getApplication();
    }

    /**
     * 删除cache数据库
     */
    public static void cleanCache() {
        cleanData(getContext(), CACHE_SP);
    }

    /**
     * 删除cache数据库
     *
     * @param context
     */
    public static void cleanCache(Context context) {
        cleanData(context, CACHE_SP);
    }

    /**
     * 删除config数据库
     */
    public static void cleanConfig() {
        cleanData(getContext(), CONFIG_SP);
    }

    /**
     * 删除config数据库
     *
     * @param context
     */
    public static void cleanConfig(Context context) {
        cleanData(context, CONFIG_SP);
    }


    /**
     * 删除app_lived_cache数据库
     */
    public static void cleanAppLivedCache() {
        cleanData(getContext(), APP_LIVED_CACHE_SP);
    }

    /**
     * 删除app_lived_cache数据库
     *
     * @param context
     */
    public static void cleanAppLivedCache(Context context) {
        cleanData(context, APP_LIVED_CACHE_SP);
    }

    /**
     * 清除cache数据
     *
     * @param key
     */
    public static void cleanCacheKey(String key) {
        cleanDataKey(getContext(), CACHE_SP, key);
    }

    /**
     * 清除cache数据
     *
     * @param context
     * @param key
     */
    public static void cleanCacheKey(Context context, String key) {
        cleanDataKey(context, CACHE_SP, key);
    }

    /**
     * 清除config数据
     *
     * @param key
     */
    public static void cleanConfigKey(String key) {
        cleanDataKey(getContext(), CONFIG_SP, key);
    }

    /**
     * 清除config数据
     *
     * @param context
     * @param key
     */
    public static void cleanConfigKey(Context context, String key) {
        cleanDataKey(context, CONFIG_SP, key);
    }

    /**
     * 删除app_lived_cache数据
     *
     * @param key
     */
    public static void cleanAppLivedCacheKey(String key) {
        cleanDataKey(getContext(), APP_LIVED_CACHE_SP, key);
    }

    /**
     * 删除app_lived_cache数据
     *
     * @param context
     * @param key
     */
    public static void cleanAppLivedCacheKey(Context context, String key) {
        cleanDataKey(context, APP_LIVED_CACHE_SP, key);
    }

    /**
     * 保存数据到cache中
     *
     * @param key
     * @param value
     */
    public static void saveToCache(String key, Object value) {
        save(getContext(), CACHE_SP, key, value);
    }

    /**
     * 保存数据到cache中
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveToCache(Context context, String key, Object value) {
        save(context, CACHE_SP, key, value);
    }

    /**
     * 保存数据到config中
     *
     * @param key
     * @param value
     */
    public static void saveToConfig(String key, Object value) {
        save(getContext(), CONFIG_SP, key, value);
    }

    /**
     * 保存数据到config中
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveToConfig(Context context, String key, Object value) {
        save(context, CONFIG_SP, key, value);
    }


    /**
     * 保存数据到app_lived_cache中
     *
     * @param key
     * @param value
     */
    public static void saveToAppLivedCache(String key, Object value) {
        save(getContext(), APP_LIVED_CACHE_SP, key, value);
    }

    /**
     * 保存数据到app_lived_cache中
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveToAppLivedCache(Context context, String key, Object value) {
        save(context, APP_LIVED_CACHE_SP, key, value);
    }

    /**
     * 判断cache中是否包含某一个key
     *
     * @param key
     * @return
     */
    public static boolean isCacheContainsKey(String key) {
        return isContainsKey(CACHE_SP, key);
    }

    public static String readStringFromCache(String key, String def) {
        return readString(getContext(), CACHE_SP, key, def);
    }

    public static String readStringFromCache(Context context, String key, String def) {
        return readString(context, CACHE_SP, key, def);
    }

    public static boolean readBooleanFromCache(String key, boolean def) {
        return readBoolean(getContext(), CACHE_SP, key, def);
    }

    public static boolean readBooleanFromCache(Context context, String key, boolean def) {
        return readBoolean(context, CACHE_SP, key, def);
    }

    public static int readIntFromCache(String key, int def) {
        return readInt(getContext(), CACHE_SP, key, def);
    }

    public static int readIntFromCache(Context context, String key, int def) {
        return readInt(context, CACHE_SP, key, def);
    }

    public static float readFloatFromCache(String key, float def) {
        return readFloat(getContext(), CACHE_SP, key, def);
    }

    public static float readFloatFromCache(Context context, String key, float def) {
        return readFloat(context, CACHE_SP, key, def);
    }

    public static long readLongFromCache(String key, long def) {
        return readLong(getContext(), CACHE_SP, key, def);
    }

    public static long readLongFromCache(Context context, String key, long def) {
        return readLong(context, CACHE_SP, key, def);
    }

    public static Set<String> readStringSetFromCache(String key, Set<String> def) {
        return readStringSet(getContext(), CACHE_SP, key, def);
    }

    public static Set<String> readStringSetFromCache(Context context, String key, Set<String> def) {
        return readStringSet(context, CACHE_SP, key, def);
    }

    public static <T> Set<T> readSetFromCache(String key, Class<T> clazz) {
        return readSet(getContext(), CACHE_SP, key, null, clazz);
    }

    public static <T> Set<T> readSetFromCache(Context context, String key, Class<T> clazz) {
        return readSet(context, CACHE_SP, key, null, clazz);
    }

    public static <T> List<T> readListFromCache(String key, Class<T> clazz) {
        return readList(getContext(), CACHE_SP, key, null, clazz);
    }

    public static <T> List<T> readListFromCache(Context context, String key, Class<T> clazz) {
        return readList(context, CACHE_SP, key, null, clazz);
    }

    public static <K, V> Map<K, V> readMapFromCache(String key, Class<K> clazzK, Class<V> clazzV) {
        return readMap(getContext(), CACHE_SP, key, null, clazzK, clazzV);
    }

    public static <K, V> Map<K, V> readMapFromCache(Context context, String key, Class<K> clazzK, Class<V> clazzV) {
        return readMap(context, CACHE_SP, key, null, clazzK, clazzV);
    }

    public static String readStringFromConfig(String key, String def) {
        return readString(getContext(), CONFIG_SP, key, def);
    }

    public static String readStringFromConfig(Context context, String key, String def) {
        return readString(context, CONFIG_SP, key, def);
    }

    public static boolean readBooleanFromConfig(String key, boolean def) {
        return readBoolean(getContext(), CONFIG_SP, key, def);
    }

    public static boolean readBooleanFromConfig(Context context, String key, boolean def) {
        return readBoolean(context, CONFIG_SP, key, def);
    }

    public static int readIntFromConfig(String key, int def) {
        return readInt(getContext(), CONFIG_SP, key, def);
    }

    public static int readIntFromConfig(Context context, String key, int def) {
        return readInt(context, CONFIG_SP, key, def);
    }

    public static float readFloatFromConfig(String key, float def) {
        return readFloat(getContext(), CONFIG_SP, key, def);
    }

    public static float readFloatFromConfig(Context context, String key, float def) {
        return readFloat(context, CONFIG_SP, key, def);
    }

    public static long readLongFromConfig(String key, long def) {
        return readLong(getContext(), CONFIG_SP, key, def);
    }

    public static long readLongFromConfig(Context context, String key, long def) {
        return readLong(context, CONFIG_SP, key, def);
    }

    public static Set<String> readSetStringFromConfig(String key, Set<String> def) {
        return readStringSet(getContext(), CONFIG_SP, key, def);
    }

    public static Set<String> readSetStringFromConfig(Context context, String key, Set<String> def) {
        return readStringSet(context, CONFIG_SP, key, def);
    }

    public static <T> Set<T> readSetFromConfig(String key, Class<T> clazz) {
        return readSet(getContext(), CONFIG_SP, key, null, clazz);
    }

    public static <T> Set<T> readSetFromConfig(Context context, String key, Class<T> clazz) {
        return readSet(context, CONFIG_SP, key, null, clazz);
    }

    public static <T> List<T> readListFromConfig(String key, Class<T> clazz) {
        return readList(getContext(), CONFIG_SP, key, null, clazz);
    }

    public static <T> List<T> readListFromConfig(Context context, String key, Class<T> clazz) {
        return readList(context, CONFIG_SP, key, null, clazz);
    }

    public static <K, V> Map<K, V> readMapFromConfig(String key, Class<K> clazzK, Class<V> clazzV) {
        return readMap(getContext(), CONFIG_SP, key, null, clazzK, clazzV);
    }

    public static <K, V> Map<K, V> readMapFromConfig(Context context, String key, Class<K> clazzK, Class<V> clazzV) {
        return readMap(context, CONFIG_SP, key, null, clazzK, clazzV);
    }

    public static String readStringFromAppLivedCache(String key, String def) {
        return readString(getContext(), APP_LIVED_CACHE_SP, key, def);
    }

    public static String readStringFromAppLivedCache(Context context, String key, String def) {
        return readString(context, APP_LIVED_CACHE_SP, key, def);
    }

    public static boolean readBooleanFromAppLivedCache(String key, boolean def) {
        return readBoolean(getContext(), APP_LIVED_CACHE_SP, key, def);
    }

    public static boolean readBooleanFromAppLivedCache(Context context, String key, boolean def) {
        return readBoolean(context, APP_LIVED_CACHE_SP, key, def);
    }

    public static int readIntFromAppLivedCache(String key, int def) {
        return readInt(getContext(), APP_LIVED_CACHE_SP, key, def);
    }

    public static int readIntFromAppLivedCache(Context context, String key, int def) {
        return readInt(context, APP_LIVED_CACHE_SP, key, def);
    }

    public static float readFloatFromAppLivedCache(String key, float def) {
        return readFloat(getContext(), APP_LIVED_CACHE_SP, key, def);
    }

    public static float readFloatFromAppLivedCache(Context context, String key, float def) {
        return readFloat(context, APP_LIVED_CACHE_SP, key, def);
    }

    public static long readLongFromAppLivedCache(String key, long def) {
        return readLong(getContext(), APP_LIVED_CACHE_SP, key, def);
    }

    public static long readLongFromAppLivedCache(Context context, String key, long def) {
        return readLong(context, APP_LIVED_CACHE_SP, key, def);
    }

    public static Set<String> readSetStringFromAppLivedCache(String key, Set<String> def) {
        return readStringSet(getContext(), APP_LIVED_CACHE_SP, key, def);
    }

    public static Set<String> readSetStringFromAppLivedCache(Context context, String key, Set<String> def) {
        return readStringSet(context, APP_LIVED_CACHE_SP, key, def);
    }

    public static <T> Set<T> readSetFromAppLivedCache(String key, Class<T> clazz) {
        return readSet(getContext(), APP_LIVED_CACHE_SP, key, null, clazz);
    }

    public static <T> Set<T> readSetFromAppLivedCache(Context context, String key, Class<T> clazz) {
        return readSet(context, APP_LIVED_CACHE_SP, key, null, clazz);
    }

    public static <T> List<T> readListFromAppLivedCache(String key, Class<T> clazz) {
        return readList(getContext(), APP_LIVED_CACHE_SP, key, null, clazz);
    }

    public static <T> List<T> readListFromAppLivedCache(Context context, String key, Class<T> clazz) {
        return readList(context, APP_LIVED_CACHE_SP, key, null, clazz);
    }

    public static <K, V> Map<K, V> readMapFromCAppLivedCache(String key, Class<K> clazzK, Class<V> clazzV) {
        return readMap(getContext(), APP_LIVED_CACHE_SP, key, null, clazzK, clazzV);
    }

    public static <K, V> Map<K, V> readMapFromAppLivedCache(Context context, String key, Class<K> clazzK, Class<V> clazzV) {
        return readMap(context, APP_LIVED_CACHE_SP, key, null, clazzK, clazzV);
    }

    /**
     * 判断cache中是否包含某一个key
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean isCacheContainsKey(Context context, String key) {
        return isContainsKey(context, CACHE_SP, key);
    }

    /**
     * 判断config中是否包含某一个key
     *
     * @param key
     * @return
     */
    public static boolean isConfigContainsKey(String key) {
        return isContainsKey(CONFIG_SP, key);
    }

    /**
     * 判断config中是否包含某一个key
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean isConfigContainsKey(Context context, String key) {
        return isContainsKey(context, CONFIG_SP, key);
    }

    /**
     * 判断app_lived_cache中是否包含某一个key
     *
     * @param key
     * @return
     */
    public static boolean isAppLivedCacheContainsKey(String key) {
        return isContainsKey(APP_LIVED_CACHE_SP, key);
    }

    /**
     * 判断app_lived_cache中是否包含某一个key
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean isAppLivedCacheContainsKey(Context context, String key) {
        return isContainsKey(context, APP_LIVED_CACHE_SP, key);
    }

    /**
     * 清除指定库
     *
     * @param context
     * @param db
     */
    public static void cleanData(Context context, String db) {
        if (context == null) {
            context = getContext();
        }
        SharedPreferences pref = context.getSharedPreferences(db, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 清除指定库中的某一个key
     *
     * @param context
     * @param db
     * @param key
     */
    public static void cleanDataKey(Context context, String db, String key) {
        SharedPreferences.Editor editor = context.getSharedPreferences(db, Context.MODE_PRIVATE).edit();
        editor.remove(key);
        editor.commit();
    }

    public static void save(String db, String key, Object value) {
        save(getContext(), db, key, value);
    }

    public static void save(Context context, String db, String key, Object value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (value == null) {
            cleanDataKey(context, db, key);
        }
        if (value instanceof String) {
            saveString(context, db, key, (String) value);
        } else if (value instanceof Integer) {
            saveInt(context, db, key, (int) value);
        } else if (value instanceof Boolean) {
            saveBoolean(context, db, key, (boolean) value);
        } else if (value instanceof Float) {
            saveFloat(context, db, key, (float) value);
        } else if (value instanceof Long) {
            saveLong(context, db, key, (long) value);
        } else if (value instanceof Set<?>) {
            saveSet(context, db, key, (Set<?>) value);
        } else if (value instanceof List<?>) {
            saveList(context, db, key, (List<?>) value);
        } else if (value instanceof Map<?, ?>) {
            saveMap(context, db, key, (Map<?, ?>) value);
        }
    }

    /**
     * 保存String到库中
     *
     * @param context
     * @param db
     * @param key
     * @param value
     */
    public static void saveString(Context context, String db, String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        SharedPreferences.Editor editor = context.getSharedPreferences(db, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 保存boolean到库中
     *
     * @param context
     * @param db
     * @param key
     * @param value
     */
    public static void saveBoolean(Context context, String db, String key, boolean value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        SharedPreferences.Editor editor = context.getSharedPreferences(db, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 保存int到库中
     *
     * @param context
     * @param db
     * @param key
     * @param value
     */
    public static void saveInt(Context context, String db, String key, int value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        SharedPreferences.Editor editor = context.getSharedPreferences(db, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 保存float到库中
     *
     * @param context
     * @param db
     * @param key
     * @param value
     */
    public static void saveFloat(Context context, String db, String key, float value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        SharedPreferences.Editor editor = context.getSharedPreferences(db, Context.MODE_PRIVATE).edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    /**
     * 保存long到库中
     *
     * @param context
     * @param db
     * @param key
     * @param value
     */
    public static void saveLong(Context context, String db, String key, long value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        SharedPreferences.Editor editor = context.getSharedPreferences(db, Context.MODE_PRIVATE).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static <T> void saveSet(Context context, String db, String key, Set<T> value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        String json = new Gson().toJson(value, new TypeToken<Set<T>>() {
        }.getType());
        saveString(context, db, key, json);
    }

    public static <T> void saveList(Context context, String db, String key, List<T> list) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        String json = new Gson().toJson(list, new TypeToken<List<T>>() {
        }.getType());
        saveString(context, db, key, json);
    }

    public static <K, V> void saveMap(Context context, String db, String key, Map<K, V> list) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        String json = new Gson().toJson(list, new TypeToken<Map<K, V>>() {
        }.getType());
        saveString(context, db, key, json);
    }

    /**
     * 读取String数据
     *
     * @param context
     * @param db
     * @param key
     * @return
     */
    public static String readString(Context context, String db, String key) {
        return readString(context, db, key, "");
    }

    /**
     * 读取String数据
     *
     * @param context
     * @param db
     * @param key
     * @param def
     * @return
     */
    public static String readString(Context context, String db, String key, String def) {
        SharedPreferences pref = context.getSharedPreferences(db, Context.MODE_PRIVATE);
        return pref.getString(key, def);
    }

    /**
     * 读取boolean数据
     *
     * @param context
     * @param db
     * @param key
     * @param def
     * @return
     */
    public static boolean readBoolean(Context context, String db, String key, boolean def) {
        SharedPreferences pref = context.getSharedPreferences(db, Context.MODE_PRIVATE);
        return pref.getBoolean(key, def);
    }

    /**
     * 读取int数据
     *
     * @param context
     * @param db
     * @param key
     * @param def
     * @return
     */
    public static int readInt(Context context, String db, String key, int def) {
        SharedPreferences pref = context.getSharedPreferences(db, Context.MODE_PRIVATE);
        return pref.getInt(key, def);
    }

    /**
     * 读取float数据
     *
     * @param context
     * @param db
     * @param key
     * @param def
     * @return
     */
    public static float readFloat(Context context, String db, String key, float def) {
        SharedPreferences pref = context.getSharedPreferences(db, Context.MODE_PRIVATE);
        return pref.getFloat(key, def);
    }

    /**
     * 读取long数据
     *
     * @param context
     * @param db
     * @param key
     * @param def
     * @return
     */
    public static long readLong(Context context, String db, String key, long def) {
        SharedPreferences pref = context.getSharedPreferences(db, Context.MODE_PRIVATE);
        return pref.getLong(key, def);
    }

    public static Set<String> readStringSet(Context context, String db, String key, Set<String> def) {
        if (isContainsKey(db, key)) {
            Type type = new ParameterizedTypeImpl(Set.class, new Class[]{String.class});
            return new Gson().fromJson(readString(context, db, key), type);
        } else {
            return def;
        }
    }

    public static <T> Set<T> readSet(Context context, String db, String key, Set<T> def, Class<T> clazz) {
        if (isContainsKey(db, key)) {
            Type type = new ParameterizedTypeImpl(Set.class, new Class[]{clazz});
            return new Gson().fromJson(readString(context, db, key), type);
        } else {
            return def;
        }
    }

    public static <T> List<T> readList(Context context, String db, String key, List<T> def, Class<T> clazz) {
        if (isContainsKey(db, key)) {
            Type type = new ParameterizedTypeImpl(List.class, new Class[]{clazz});
            return new Gson().fromJson(readString(context, db, key), type);
        } else {
            return def;
        }
    }

    public static <K, V> Map<K, V> readMap(Context context, String db, String key, Map<K, V> def, Class<K> clazzK, Class<V> clazzV) {
        if (isContainsKey(db, key)) {
            Type type = new ParameterizedTypeImpl(Map.class, new Class[]{clazzK, clazzV});
            return new Gson().fromJson(readString(context, db, key), type);
        } else {
            return def;
        }
    }

    /**
     * 查询数据库是否包含key
     *
     * @param db
     * @param key
     * @return
     */
    public static boolean isContainsKey(String db, String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        SharedPreferences pref = getContext().getSharedPreferences(db, Context.MODE_PRIVATE);
        return pref.contains(key);
    }

    /**
     * 查询数据库是否包含key
     *
     * @param context
     * @param db
     * @param key
     * @return
     */
    public static boolean isContainsKey(Context context, String db, String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        SharedPreferences pref = context.getSharedPreferences(db, Context.MODE_PRIVATE);
        return pref.contains(key);
    }
}
