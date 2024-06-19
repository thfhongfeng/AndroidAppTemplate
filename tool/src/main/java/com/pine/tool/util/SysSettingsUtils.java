package com.pine.tool.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.WindowManager;

public class SysSettingsUtils {
    private static final String TAG = "SysSettingsUtils";

    /**
     * 可调节的最小亮度值
     */
    public static final int MIN_BRIGHTNESS = 30;
    /**
     * 可调节的最大亮度值
     */
    public static final int MAX_BRIGHTNESS = 255;

    /**
     * 获得当前系统的亮度模式
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
     */
    public static int getBrightnessMode(Context context) {
        int brightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
        try {
            brightnessMode = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            LogUtils.d(TAG, "getBrightnessMode brightnessMode:" + brightnessMode);
        } catch (Exception e) {
            LogUtils.d(TAG, "getBrightnessMode Exception:" + e);
        }
        return brightnessMode;
    }

    /**
     * 设置当前系统的亮度模式
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
     */
    public static void setBrightnessMode(Context context, int brightnessMode) {
        try {
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, brightnessMode);
            LogUtils.d(TAG, "setBrightnessMode brightnessMode:" + brightnessMode);
        } catch (Exception e) {
            LogUtils.d(TAG, "setBrightnessMode Exception:" + e);
        }
    }

    /**
     * 获得当前系统的亮度值:0-255
     */
    public static int getSysScreenBrightness(Context context) {
        int screenBrightness = MAX_BRIGHTNESS;
        try {
            screenBrightness = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
        }
        LogUtils.d(TAG, "getSysScreenBrightness screenBrightness:" + screenBrightness);
        return screenBrightness;
    }

    /**
     * 获得当前系统的亮度值百分比
     */
    public static int getSysScreenBrightnessPct(Context context) {
        int screenBrightness = MAX_BRIGHTNESS;
        try {
            screenBrightness = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
        }
        int screenBrightnessPct = screenBrightness * 100 / MAX_BRIGHTNESS;
        LogUtils.d(TAG, "setSysScreenBrightness screenBrightness:" + screenBrightness +
                ",screenBrightnessPct:" + screenBrightnessPct);
        return screenBrightnessPct;
    }

    /**
     * 设置当前系统的亮度值:0-255
     *
     * @param context
     * @param brightness
     */
    public static int setSysScreenBrightness(Context context, int brightness) {
        try {
            if (brightness < MIN_BRIGHTNESS) {
                brightness = MIN_BRIGHTNESS;
            }
            if (brightness > MAX_BRIGHTNESS) {
                brightness = MAX_BRIGHTNESS;
            }
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
            Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
            resolver.notifyChange(uri, null); // 实时通知改变
            LogUtils.d(TAG, "setSysScreenBrightness brightness:" + brightness);
        } catch (Exception e) {
            LogUtils.d(TAG, "setSysScreenBrightness Exception:" + e);
        }
        return brightness;
    }

    /**
     * 设置当前系统的亮度值百分比
     *
     * @param context
     * @param brightnessPct 百分比：0-100
     */
    public static int setSysScreenBrightnessPct(Context context, int brightnessPct) {
        int brightness = brightnessPct * MAX_BRIGHTNESS / 100;
        try {
            if (brightness < MIN_BRIGHTNESS) {
                brightness = MIN_BRIGHTNESS;
            }
            if (brightness > MAX_BRIGHTNESS) {
                brightness = MAX_BRIGHTNESS;
            }
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
            Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
            resolver.notifyChange(uri, null); // 实时通知改变
            LogUtils.d(TAG, "setSysScreenBrightnessPct brightnessPct:" + brightnessPct +
                    ", brightness:" + brightness);
        } catch (Exception e) {
            LogUtils.d(TAG, "setSysScreenBrightnessPct Exception:" + e);
        }
        return brightness * 100 / MAX_BRIGHTNESS;
    }

    /**
     * 设置屏幕亮度百分比，这会反映到真实屏幕上
     *
     * @param activity
     * @param brightnessPct 百分比：0-100
     */
    public static int setActScreenBrightnessPct(final Activity activity, int brightnessPct) {
        int brightness = brightnessPct * MAX_BRIGHTNESS / 100;
        if (brightness < MIN_BRIGHTNESS) {
            brightness = MIN_BRIGHTNESS;
        }
        if (brightness > MAX_BRIGHTNESS) {
            brightness = MAX_BRIGHTNESS;
        }
        final WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = brightness / (float) MAX_BRIGHTNESS;
        LogUtils.d(TAG, "setActScreenBrightnessPct brightnessPct:" + brightnessPct +
                ", lp.screenBrightness:" + lp.screenBrightness);
        activity.getWindow().setAttributes(lp);
        return brightness * 100 / MAX_BRIGHTNESS;
    }

    /**
     * 获得当前音乐音量
     */
    public static int getMusicVolume(Context context) {
        int volume = getVolume(context, AudioManager.STREAM_MUSIC);
        return volume;
    }

    /**
     * 获得当前音乐音量百分比
     */
    public static int getMusicVolumePct(Context context) {
        int volumePct = getVolumePct(context, AudioManager.STREAM_MUSIC);
        return volumePct;
    }

    /**
     * 设置音乐音量
     *
     * @param context
     * @param volume
     */
    public static int setMusicVolume(Context context, int volume) {
        return setVolume(context, volume, AudioManager.STREAM_MUSIC);
    }

    /**
     * 设置音乐音量百分比
     *
     * @param context
     * @param volumePct 百分比：0-100
     */
    public static int setMusicVolumePct(Context context, int volumePct) {
        return setVolumePct(context, volumePct, AudioManager.STREAM_MUSIC);
    }

    /**
     * 获得当前tts音量
     */
    public static int getTtsVolume(Context context) {
        int volume = getVolume(context, AudioManager.STREAM_NOTIFICATION);
        return volume;
    }

    /**
     * 获得当前tts音量百分比
     */
    public static int getTtsVolumePct(Context context) {
        int volumePct = getVolumePct(context, AudioManager.STREAM_NOTIFICATION);
        return volumePct;
    }

    /**
     * 设置tts音量
     *
     * @param context
     * @param volume
     */
    public static int setTtsVolume(Context context, int volume) {
        return setVolume(context, volume, AudioManager.STREAM_NOTIFICATION);
    }

    /**
     * 设置tts音量百分比
     *
     * @param context
     * @param volumePct 百分比：0-100
     */
    public static int setTtsVolumePct(Context context, int volumePct) {
        return setVolumePct(context, volumePct, AudioManager.STREAM_NOTIFICATION);
    }

    /**
     * 获得当前指定声道音量
     *
     * @param steamType
     */
    public static int getVolume(Context context, int steamType) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int volume = mAudioManager.getStreamVolume(steamType);
        LogUtils.d(TAG, "getVolume volume:" + volume + ", steamType:" + steamType);
        return volume;
    }

    /**
     * 获得当前指定声道音量百分比
     *
     * @param steamType
     */
    public static int getVolumePct(Context context, int steamType) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(steamType);
        int volume = mAudioManager.getStreamVolume(steamType);
        int volumePct = volume * 100 / maxVolume;
        LogUtils.d(TAG, "getVolumePct volume:" + volume + ",maxVolume:" + maxVolume +
                ",volumePct:" + volumePct + ", steamType:" + steamType);
        return volumePct;
    }

    /**
     * 设置指定声道音量
     *
     * @param context
     * @param volume
     * @param steamType
     */
    public static int setVolume(Context context, int volume, int steamType) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(steamType);
        if (volume < 0) {
            volume = 0;
        }
        if (volume > maxVolume) {
            volume = maxVolume;
        }
        LogUtils.d(TAG, "setVolume volume:" + volume + ",maxVolume:" + maxVolume +
                ", steamType:" + steamType);
        mAudioManager.setStreamVolume(steamType, volume, 0);
        return volume;
    }

    /**
     * 设置指定声道音量百分比
     *
     * @param context
     * @param volumePct 百分比：0-100
     * @param steamType
     */
    public static int setVolumePct(Context context, int volumePct, int steamType) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(steamType);
        int volume = volumePct * maxVolume / 100;
        if (volume < 0) {
            volume = 0;
        }
        if (volume > maxVolume) {
            volume = maxVolume;
        }
        LogUtils.d(TAG, "setVolumePct volume:" + volume + ",maxVolume:" + maxVolume +
                ",volumePct:" + volumePct + ", steamType:" + steamType);
        mAudioManager.setStreamVolume(steamType, volume, 0);
        if (maxVolume > 0) {
            return volume * 100 / maxVolume;
        }
        return 0;
    }

    /**
     * 获取系统休眠时间
     */
    public static int getSleepTime(Context context) {
        int result = 0;
        try {
            result = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 设置休眠时间
     *
     * @param context
     * @param sleepTime
     */
    public static int setSleepTime(Context context, int sleepTime) {
        if (sleepTime <= 0) {
            sleepTime = Integer.MAX_VALUE;
        }
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, sleepTime);
        Uri uri = Settings.System
                .getUriFor(Settings.System.SCREEN_OFF_TIMEOUT);
        context.getContentResolver().notifyChange(uri, null);
        return sleepTime;
    }
}
